package br.ufla.inovatech.service;

public final class Sanitizer {

    private Sanitizer() {}

    /** Remove espaços extras e caracteres de controle. */
    public static String texto(String valor) {
        if (valor == null) return null;
        return valor
                .replaceAll("\\p{Cntrl}", "")
                .replaceAll("<[^>]*>", "")
                .trim()
                .replaceAll("\\s+", " ");
    }

    /** E-mail sem espaços e em minúsculas. */
    public static String email(String valor) {
        if (valor == null) return null;
        return valor.trim().toLowerCase();
    }

    /** CPF só com números (remove pontos e traço). */
    public static String cpf(String valor) {
        if (valor == null) return null;
        return valor.replaceAll("\\D", "");
    }
}