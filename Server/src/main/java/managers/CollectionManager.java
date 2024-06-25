package managers;

import models.*;


import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Работа с коллекцией.
 */
public class CollectionManager {

    private ArrayList<Movie> collection = new ArrayList<Movie>();
    private ArrayDeque<String> logStack = new ArrayDeque<String>();
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;
    private WithCsvManager withCsvManager;

    public CollectionManager(WithCsvManager withCsvManager) {
        this.lastInitTime = lastInitTime;
        this.lastSaveTime = lastSaveTime;
        this.withCsvManager = withCsvManager;
    }

    public CollectionManager() {

    }

    /**
     * @return последнее время инициализации.
     */
    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    /**
     * @return последнее время сохранения.
     */
    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * @return коллекции.
     */
    public synchronized List<Movie> getCollection() {
        return new ArrayList<>(collection);
    }

    /**
     * Сохраняет коллекцию в файл
     */
    public void save() {
        withCsvManager.writeCollection(collection);
        lastSaveTime = LocalDateTime.now();
    }

    /**
     * Получает элемент по ID.
     *
     * @param id Movie
     * @return movie по id
     */
    public synchronized Movie byId(long id) {
        for (Movie movie : collection) {
            if (movie.getId() == id) {
                return movie;
            }
        }
        return null;
    }

    /**
     * Проверяет содержит ли коллекция Movie.
     * @param m Movie
     * @return Movie m
     */
    public boolean isСontain(Movie m) {
        return m == null || byId((int) m.getId()) != null;
    }

    /**
     * Получает свободный ID.
     *
     * @return id
     */
    public int getFreeId() {
        int id = 0;
        while (byId(++id) != null);
        return id;
    }


    /**
     * Добавляет Movie.
     *
     * @param movie Movie
     * @return true
     */
    public synchronized int add(Movie movie) {
        String insertCoordinatesSQL = "INSERT INTO Coordinates (x, y) VALUES (?, ?) RETURNING id";
        String insertPersonSQL = "INSERT INTO Person (name, birthday, color, nationality) VALUES (?, ?, ?, ?) RETURNING id";
        String insertMovieSQL = "INSERT INTO Movies (name, coordinates_id, oscars_count, genre, mpaa_rating, director_id, user_id) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id ";

        try (Connection connection = ConnectionManager.open(); PreparedStatement coordinatesStmt = connection.prepareStatement(insertCoordinatesSQL);
             PreparedStatement personStmt = connection.prepareStatement(insertPersonSQL);
             PreparedStatement movieStmt = connection.prepareStatement(insertMovieSQL)) {

            // Inserting Coordinates
            coordinatesStmt.setLong(1, movie.getCoordinates().getX());
            coordinatesStmt.setInt(2, movie.getCoordinates().getY());
            ResultSet coordinatesRs = coordinatesStmt.executeQuery();
            coordinatesRs.next();
            long coordinatesId = coordinatesRs.getLong(1);

            // Inserting Person
            personStmt.setString(1, movie.getDirector().getName());
            personStmt.setDate(2, Date.valueOf(movie.getDirector().getBirthday()));
            personStmt.setString(3, movie.getDirector().getColor().name());
            personStmt.setString(4, movie.getDirector().getNationality().name());
            ResultSet personRs = personStmt.executeQuery();
            personRs.next();
            long directorId = personRs.getLong(1);

            // Inserting Movie
            movieStmt.setString(1, movie.getName());
            movieStmt.setLong(2, coordinatesId);
            movieStmt.setLong(3, movie.getOscarsCount());
            movieStmt.setString(4, movie.getGenre().name());
            if (movie.getMpaaRating() != null) {
                movieStmt.setString(5, movie.getMpaaRating().name());
            } else {
                movieStmt.setNull(5, Types.VARCHAR);
            }
            movieStmt.setLong(6, directorId);
            movieStmt.setInt(7, movie.getUser().getId());
            ResultSet resultSet = movieStmt.executeQuery();
            resultSet.next();
            int movieId = resultSet.getInt(1);
            // Add to collection if database insert is successful
            collection.add(movie);
            return movieId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @param id ID элемента.
     * @return Проверяет, существует ли элемент с таким ID.
     */
    public boolean checkExist(int id) {
        return byId(id) != null;
    }

    /**
     * Обновляет Movie.
     *
     * @param movie Movie
     * @return true
     */
    public synchronized boolean update(Movie movie) {
        String updateCoordinatesSQL = "UPDATE Coordinates SET x = ?, y = ? WHERE id = ?";
        String updatePersonSQL = "UPDATE Person SET name = ?, birthday = ?, color = ?, nationality = ? WHERE id = ?";
        String updateMovieSQL = "UPDATE Movies SET name = ?, oscars_count = ?, genre = ?, mpaa_rating = ?, user_id = ? WHERE id = ?";

        try (Connection connection = ConnectionManager.open();
             PreparedStatement coordinatesStmt = connection.prepareStatement(updateCoordinatesSQL);
             PreparedStatement personStmt = connection.prepareStatement(updatePersonSQL);
             PreparedStatement movieStmt = connection.prepareStatement(updateMovieSQL)) {

            // Updating Coordinates
            coordinatesStmt.setLong(1, movie.getCoordinates().getX());
            coordinatesStmt.setInt(2, movie.getCoordinates().getY());
            coordinatesStmt.setLong(3, movie.getCoordinates().getId());
            coordinatesStmt.executeUpdate();

            // Updating Person
            personStmt.setString(1, movie.getDirector().getName());
            personStmt.setDate(2, Date.valueOf(movie.getDirector().getBirthday()));
            personStmt.setString(3, movie.getDirector().getColor().name());
            personStmt.setString(4, movie.getDirector().getNationality().name());
            personStmt.setLong(5, movie.getDirector().getId());
            personStmt.executeUpdate();

            // Updating Movie
            movieStmt.setString(1, movie.getName());
            movieStmt.setLong(2, movie.getOscarsCount());
            movieStmt.setString(3, movie.getGenre().name());
            if (movie.getMpaaRating() != null) {
                movieStmt.setString(4, movie.getMpaaRating().name());
            } else {
                movieStmt.setNull(4, Types.VARCHAR);
            }
            movieStmt.setString(5, movie.getUser().getLogin());
            movieStmt.setLong(6, movie.getId());
            movieStmt.executeUpdate();

            // Update collection if database update is successful
            int index = collection.indexOf(movie);
            if (index != -1) {
                collection.set(index, movie);
            } else {
                collection.add(movie);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удаляет Movie по ID.
     *
     * @param id Movie
     * @return true
     */
    public synchronized boolean remove(long id) {
        String deleteMovieSQL = "DELETE FROM Movies WHERE id = ?";
        try (Connection connection = ConnectionManager.open();
             PreparedStatement stmt = connection.prepareStatement(deleteMovieSQL)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                collection.removeIf(movie -> movie.getId() == id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Создает транзакцию или добавляет операцию в транзакцию.
     * @param cmd     String
     * @param isFirst boolean
     */
    public void addLog(String cmd, boolean isFirst) {
        if (isFirst)
            logStack.push("+");
        if (!cmd.equals(""))
            logStack.push(cmd);
    }

    /**
     * Фиксирует изменения коллекции.
     */
    public void update() {
        Collections.sort(collection);
    }



    /**
     * Загружает коллекцию из файла.
     * @return true, если загрузка прошла успешно, иначе false
     */
    public synchronized boolean loadCollection() {
        collection.clear();
        String query = "SELECT m.id, m.name, m.coordinates_id, m.creation_date, m.oscars_count, m.genre, m.mpaa_rating, " +
                       "m.director_id, m.user_id, " +
                       "c.id AS coordinates_id, c.x, c.y, " +
                       "p.id AS person_id, p.name AS director_name, p.birthday AS director_birthday, p.color AS director_color, p.nationality AS director_nationality, " +
                       "u.id AS user_id, u.login AS user_login " +
                       "FROM Movies m " +
                       "JOIN Coordinates c ON m.coordinates_id = c.id " +
                       "JOIN Person p ON m.director_id = p.id " +
                       "JOIN Users u ON m.user_id = u.id";

        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                LocalDateTime creationDate = rs.getTimestamp("creation_date").toLocalDateTime();
                long oscarsCount = rs.getLong("oscars_count");
                MovieGenre genre = MovieGenre.valueOf(rs.getString("genre"));
                MpaaRating mpaaRating = rs.getString("mpaa_rating") != null ? MpaaRating.valueOf(rs.getString("mpaa_rating")) : null;

                long coordinatesId = rs.getLong("coordinates_id");
                int x = rs.getInt("x");
                int y = rs.getInt("y");

                long personId = rs.getLong("person_id");
                String directorName = rs.getString("director_name");
                LocalDate directorBirthday = rs.getDate("director_birthday") != null ? LocalDate.from(rs.getDate("director_birthday").toLocalDate()
                        .atStartOfDay()) : null;
                Color directorColor = Color.valueOf(rs.getString("director_color"));
                Country directorNationality = Country.valueOf(rs.getString("director_nationality"));

                int userId = rs.getInt("user_id");
                String userLogin = rs.getString("user_login");

                Coordinates coordinates = new Coordinates(x, y);
                coordinates.setId((int) coordinatesId);

                Person director = new Person(directorName, directorBirthday, directorColor, directorNationality);
                director.setId((int) personId);

                User user = new User(userLogin, ""); // Пароль не нужен для загрузки коллекции
                user.setId(userId);

                Movie movie = new Movie(id, name, coordinates, creationDate, oscarsCount, genre, mpaaRating, director);
                movie.setUser(user);
                collection.add(movie);
            }

            lastInitTime = LocalDateTime.now();
            update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Возвращает отсортированную коллекцию фильмов по размеру.
     * @return отсортированная коллекция фильмов
     */
    public synchronized List<Movie> sortedBySize() {
        return collection.stream()
                .sorted(Comparator.comparingInt(Movie::getSize))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает отсортированную коллекцию фильмов по ID.
     *
     * @return отсортированная коллекция фильмов
     */
    public synchronized List<Movie> sortedById() {
        return collection.stream()
                .sorted(Comparator.comparingLong(Movie::getId))
                .collect(Collectors.toList());
    }

        /**
         * Ищет максимальное значение по полю oscarsCount
         * @return maxOscars
         */
        public synchronized long findMax() {
            return collection.stream()
                    .mapToLong(Movie::getOscarsCount)
                    .max()
                    .orElse(0);
        }


    /**
     * Мешает коллекцию.
     */
    public synchronized List<Movie> shuffle() {
        collection.stream()
                .collect(Collectors.toList());
        if (collection.size() == 2) {
            Collections.swap(collection, 0, 1);
        } else {
            Collections.shuffle(collection);

        }
        return  collection;
    }

    /**
     * Очищает коллекцию, возвращая массив идентификаторов фильмов.
     *
     * @return массив ид фильмов
     */
    public long[] clear() {
        return collection.stream()
                .mapToLong(Movie::getId)
                .toArray();
    }


        /**
         * Выводит элементы, значение поля genre которых меньше заданного
         * @param genre MovieGenre
         * @return Array y, состоящий из ID
         */
        public synchronized long[] minGenre(MovieGenre genre) {
            return collection.stream()
                    .filter(m -> m.getGenre().compareTo(genre) < 0)
                    .mapToLong(Movie::getId)
                    .toArray();
        }


        /**
         * Сортирует поля oscarsCount
         * @return Array y, состоящий из отсортированных коллекций.
         */
        public synchronized long[] fieldOscars () {
            long x[] = new long[collection.size()];
            int count = 0;
            for (var mov : collection) {
                x[count] += mov.getOscarsCount();
                count += 1;
            }
            Arrays.sort(x);
            long y[] = new long[collection.size()];
            for (int i = collection.size() - 1; i >= 0; i--) {
                y[i] = x[collection.size() - i - 1];
            }
            return y;
        }

        @Override
        public String toString () {
            if (collection.isEmpty()) return "Коллекция пуста!";

            StringBuilder info = new StringBuilder();
            for (var m : collection) {
                info.append(m.toString() + "\n\n");
            }
            return info.toString().trim();
        }


    }