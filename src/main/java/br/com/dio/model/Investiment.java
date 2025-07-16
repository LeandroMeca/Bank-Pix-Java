package br.com.dio.model;

public record Investiment(long id, long tax, long initialFounds) {

    @Override
    public String toString() {
        return "Investiment{" +
                "id=" + id +
                ", tax=" + tax + "%"+
                ", initialFounds=" + (initialFounds / 100) +","+ (initialFounds % 100)+
                '}';
    }
}
