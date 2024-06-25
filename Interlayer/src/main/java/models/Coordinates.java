package models;


import utilities.Validatable;

import java.io.Serializable;

/**
 * Класс Coordinates.
 */
public class Coordinates implements Validatable, Serializable {

    private int id;
    private long x;
    private int y; //Максимальное значение поля: 72

    public Coordinates(long x, int y) {
        this.x = x;
        this.y = y;
    }
    public Coordinates(String s) {
        try {
            try { this.x = Long.parseLong(s.split(";")[0]); } catch (NumberFormatException e) { }
            try { this.y = Integer.parseInt(s.split(";")[1]); }catch (NumberFormatException e) { }
        } catch (ArrayIndexOutOfBoundsException e) {}
    }


    @Override
    public String toString() {
        return x + ";" + y;
    }


    /**
     * Проверка на корректность полей класса Coordinates.
     */
    @Override
    public boolean validate() {
        if (y > 72) return false;
        else return true;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
