package br.ufla.inovatech.testeCaixaPreta;

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
}
