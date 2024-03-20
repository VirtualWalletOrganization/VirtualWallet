package com.example.virtualwallet.models.enums;

public enum CountryCodes {

    BG,
    DE,
    FR,
    UK,
    US;

    public String getCode() {
        switch (this) {
            case BG:
                return "359";
            case DE:
                return "49";
            case FR:
                return "33";
            case UK:
                return "44";
            case US:
                return "1";
            default:
                return "0";
        }
    }

    @Override
    public String toString() {
        return String.format("%s (+%s)", name(), getCode());
    }

    public static CountryCodes fromCode(String code) {
        switch (code) {
            case "359":
                return BG;
            case "49":
                return DE;
            case "33":
                return FR;
            case "44":
                return UK;
            case "1":
            default:
                return US;
        }
    }
}