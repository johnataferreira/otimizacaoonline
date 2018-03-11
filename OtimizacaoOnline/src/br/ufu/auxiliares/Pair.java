package br.ufu.auxiliares;

/**
 * Classe auxiliar para implementar o algoritmo de otimização online List-scheduling. 
 * */
public class Pair <Carga extends Comparable <Carga>, IdMaquina extends Comparable <IdMaquina>>  implements Comparable <Pair<Carga, IdMaquina>>  {
    private Carga carga;
    private IdMaquina idMaquina;

    public Pair(Carga carga, IdMaquina idMaquina) { 
    	this.carga = carga; 
    	this.idMaquina = idMaquina; 
    }

    public Carga getCarga() { 
    	return carga; 
    }

    public IdMaquina getIdMaquina() { 
    	return idMaquina; 
    }

    public int compareTo (Pair<Carga, IdMaquina>  obj) {
    	//Ordeno primeiro pela carga e depois pelo id da maquina
    	if (carga.compareTo(obj.getCarga()) == 0) {
    		return idMaquina.compareTo(obj.getIdMaquina());
    	}

    	return carga.compareTo(obj.getCarga());
    }

    public String toString() { 
    	return "{" + carga + "," + idMaquina + "}"; 
    }
}