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

    /** Tamanho máximo aceito para um token, evitando processar entradas gigantes. */
    private static final int TAMANHO_MAXIMO_TOKEN = 4096;

    /** Formato de um JWT: três partes em Base64URL separadas por ponto. */
    private static final java.util.regex.Pattern FORMATO_JWT =
            java.util.regex.Pattern.compile("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");

    /**
     * Limpa o token JWT recebido (ex.: do cabeçalho Authorization).
     * Remove espaços nas pontas e o prefixo "Bearer " (sem diferenciar maiúsculas/minúsculas).
     * Retorna null se o token estiver vazio, for grande demais ou não tiver o formato de um JWT.
     */
    public static String token(String valor) {
        if (valor == null) return null;

        String token = valor.trim();
        if (token.regionMatches(true, 0, "Bearer ", 0, 7)) {
            token = token.substring(7).trim();
        }

        if (token.isEmpty() || token.length() > TAMANHO_MAXIMO_TOKEN) return null;
        if (!FORMATO_JWT.matcher(token).matches()) return null;

        return token;
    }
}