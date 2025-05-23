package org.example;

public class Main {
    /**
     *  Основная точка входа в программу.  Выводит приветствие и числа от 1 до 5.
     *
     * @param args Аргументы командной строки (не используются в данном случае).
     */
    public static void main(String[] args) {
        greetUser();
        printNumbers();
    }

    /**
     * Выводит приветствие пользователю.
     */
    private static void greetUser() {
        System.out.println("Hello and welcome!");
    }

    /**
     * Выводит последовательность чисел в заданном диапазоне.
     */
    private static void printNumbers() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }
}