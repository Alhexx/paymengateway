package com.challenge.paymengateway.common.utils;

public class CPFUtils {

    public static boolean isValidCPF(String cpf) {
        if (cpf == null) return false;

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) soma += (cpf.charAt(i) - '0') * (10 - i);
            int resto = 11 - (soma % 11);
            if (resto == 10 || resto == 11) resto = 0;
            if (resto != (cpf.charAt(9) - '0')) return false;

            soma = 0;
            for (int i = 0; i < 10; i++) soma += (cpf.charAt(i) - '0') * (11 - i);
            resto = 11 - (soma % 11);
            if (resto == 10 || resto == 11) resto = 0;

            return resto == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }
}
