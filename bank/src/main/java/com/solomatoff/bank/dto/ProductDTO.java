package com.solomatoff.bank.dto;


import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public enum ProductDTO {
    /* enum без значений */;

    private interface Id { @Positive Long getId(); }
    private interface Name { @NotBlank String getName(); }
    private interface Price { @Positive Double getPrice(); }
    private interface Cost { @Positive Double getCost(); }

    public enum Request {
        /* enum без значений */;

        @Value
        public static class Create implements Name, Price, Cost {
            String name;
            Double price;
            Double cost;
        }
    }

    public enum Response {
        /* enum без значений */;

        @Value
        public static class Public implements Id, Name, Price {
            Long id;
            String name;
            Double price;
        }

        @Value
        public static class Private implements Id, Name, Price, Cost {
            Long id;
            String name;
            Double price;
            Double cost;
        }
    }
}