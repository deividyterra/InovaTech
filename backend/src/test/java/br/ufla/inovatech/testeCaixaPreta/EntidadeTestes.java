package br.ufla.inovatech.testeCaixaPreta;

import java.util.Locale;

public abstract class EntidadeTestes {

    private  Integer id;

    public EntidadeTestes(Integer id){
        this.id = id;
    }

    public EntidadeTestes(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Nome da tabela no banco. Por padrão é o nome simples da classe em minúsculas
     * (Usuario -> usuario). Sobrescreva se for diferente.
     * Não é um getter de propósito, para não ir no JSON enviado à API.
     */
    public String tabela() {
        return getClass().getSimpleName().toLowerCase(Locale.ROOT);
    }

    /**
     * Nome da coluna de chave primária. Por padrão "id_" + tabela (usuario -> id_usuario).
     * Sobrescreva quando não seguir o padrão (ex.: item_pedido usa id_item).
     */
    public String colunaId() {
        return "id_" + tabela();
    }
}
