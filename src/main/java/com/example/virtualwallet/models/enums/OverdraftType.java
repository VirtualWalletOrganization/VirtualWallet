package com.example.virtualwallet.models.enums;

public enum OverdraftType {

    STANDARD(1000, 0.01, 1),
    PREMIUM(3000, 0.03, 6),
    PLATINUM(6000, 0.06, 12);

    private final int limit;
    private final double interestRate;
    private final int durationInMonths;

    OverdraftType(int limit, double interestRate, int durationInMonths) {
        this.limit = limit;
        this.interestRate = interestRate;
        this.durationInMonths = durationInMonths;
    }

    public int getLimit() {
        return limit;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }
}