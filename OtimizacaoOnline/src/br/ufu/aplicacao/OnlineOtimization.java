package br.ufu.aplicacao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import br.ufu.auxiliares.Pair;
/**
 * Classe respons�vel por executar os algoritmos implementados para este TCC:
 * "Algoritmos Online para Escalonamento de Tarefas em Sistemas Multiprocessados".
 * */
public class OnlineOtimization {
	public static final int		QTD_TAREFAS_DEFAULT 		= 10;
	public static final int		QTD_MAQUINAS_DEFAULT 		= 3;

	public static final int		CARGA_INICIAL_DA_MAQUINA 	= 0;

	public static final String 	LIST_SCHEDULING 			= "List-scheduling";
	public static final String 	RANDOM 						= "Random";

	public static final int 	VETOR_TAREFA_CRESCENTE		= 1;
	public static final int 	VETOR_TAREFA_NORMAL			= 0;
	public static final int 	VETOR_TAREFA_DECRESCENTE	= -1;

	public static void main(String[] args) {
		try {
			int qtdTarefas = getValueFromArgs(args, 0, QTD_TAREFAS_DEFAULT);
			int qtdMaquinas = getValueFromArgs(args, 1, QTD_MAQUINAS_DEFAULT);
		
			//int qtdTarefas = gerarQuantidadeAleatorias(100, 1000);
			//int qtdMaquinas = gerarQuantidadeAleatorias(5, 50);

			int [] vetCargaDasTarefass = getCargaInicial(qtdTarefas); //Cargas aleat�rias entre 10 e 100

			execute(qtdMaquinas, qtdTarefas, vetCargaDasTarefass, VETOR_TAREFA_NORMAL);
			execute(qtdMaquinas, qtdTarefas, vetCargaDasTarefass, VETOR_TAREFA_CRESCENTE);
			execute(qtdMaquinas, qtdTarefas, vetCargaDasTarefass, VETOR_TAREFA_DECRESCENTE);

		} catch (Exception e) {
			System.out.println("Erro inesperado: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Ir� devolver o valor inteiro presente no vetor referente o ind�ce passado como par�metro. Caso o vetor esteja vazio 
	 * ir� devolver o valor default.
	 * */
	public static int getValueFromArgs(String [] args, int indice, int defaultValue) throws Exception {
		int result = defaultValue;

		try {
			result = Integer.parseInt(args[indice]);
		} catch (ArrayIndexOutOfBoundsException ignored) {
		} catch (NumberFormatException ignored) {
		}

		return result;
	}

	/**
	 * M�todo que ir� gerar valores aleat�rios para quantidade de tarefas e para quantidade de m�quinas. Passar como par�metro 
	 * um limite inferior e um superior. 
	 * */
	public static int gerarQuantidadeAleatorias(int limiteInferior, int limiteSuperior) throws Exception {
		return (int) (Math.random() * limiteSuperior) + limiteInferior;
	}

	/**
	 * M�todo respons�vel por retornar um vetor de inteiros com valores aleat�rios da carga de tarefas entre 10 e 100.
	 * A quantidade de valores ser� passada como par�metros.
	 * */
	public static int[] getCargaInicial(int qtdTarefas) throws Exception {
		int [] vetCargaDasTarefass = new int[qtdTarefas];

		for (int i = 0; i < qtdTarefas; i++) {
			//Gerando carga de tarefa entre 10 e 100
			vetCargaDasTarefass[i] = (int) (Math.random() * 90) + 10;
		}
	
		return vetCargaDasTarefass;
	}

	/**
	 * M�todo respos�vel por executar os algoritmos (List-scheduling e Random) conforme configura��es passadas como par�metro.
	 * */
	public static void execute(int qtdMaquinas, int qtdTarefas, int[] vetCargaDasTarefas, int tipoOrdenacao) throws Exception {
		if (tipoOrdenacao != VETOR_TAREFA_NORMAL && tipoOrdenacao != VETOR_TAREFA_CRESCENTE && tipoOrdenacao != VETOR_TAREFA_DECRESCENTE) {
			throw new Exception("Nao foi possivel ordenar o vetor. Verifique o tipo de ordenacao passado como parametro!");
		}

		if (tipoOrdenacao == VETOR_TAREFA_CRESCENTE) {
			ordenacaoVetorInteiro(vetCargaDasTarefas, VETOR_TAREFA_CRESCENTE);

		} else if (tipoOrdenacao == VETOR_TAREFA_DECRESCENTE) {
			ordenacaoVetorInteiro(vetCargaDasTarefas, VETOR_TAREFA_DECRESCENTE);
		}

		printCargaInicial(vetCargaDasTarefas, tipoOrdenacao);

		try {
			listSchedluling(qtdMaquinas, qtdTarefas, vetCargaDasTarefas);
		} catch (Exception e) {
			System.out.println("Erro ao executar o algoritmo " + LIST_SCHEDULING + ": " + e.getMessage());
			e.printStackTrace();
		}

		try {
			random(qtdMaquinas, qtdTarefas, vetCargaDasTarefas);
		} catch (Exception e) {
			System.out.println("Erro ao executar o algoritmo " + RANDOM + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Esse m�todo � respons�vel por ordenar o vetor conforme par�metro passado. O vetor poder� ser ordenado de forma crescente (1), 
	 * decrescente (-1) ou normal (0). Se for passado um valor diferente disso, um erro ser� lan�ado.
	 * */
	public static void ordenacaoVetorInteiro(int[] vetorParaOrdenar, int tipoOrdenacao) throws Exception {
		if (tipoOrdenacao != VETOR_TAREFA_DECRESCENTE && tipoOrdenacao != VETOR_TAREFA_CRESCENTE) {
			throw new Exception("Nao foi possivel ordenar o vetor. Verifique o tipo de ordenacao passado como parametro!");
		}

		int aux = 0;

		for (int i = 0; i < vetorParaOrdenar.length; i++) {
			for (int j = 0; j < vetorParaOrdenar.length; j++) {
				if (tipoOrdenacao == VETOR_TAREFA_CRESCENTE) {
					if (vetorParaOrdenar[i] < vetorParaOrdenar[j]) {
						aux = vetorParaOrdenar[i];
						vetorParaOrdenar[i] = vetorParaOrdenar[j];
						vetorParaOrdenar[j] = aux;
					}
				} else {
					if (vetorParaOrdenar[i] > vetorParaOrdenar[j]) {
						aux = vetorParaOrdenar[i];
						vetorParaOrdenar[i] = vetorParaOrdenar[j];
						vetorParaOrdenar[j] = aux;
					}
				}
			}
		}
	}

	/**
	 * M�todo respons�vel por iniciar a carga do vetor conforme configura��o e printar os valores no console.
	 * */
	public static void printCargaInicial(int[] vetCargaDasTarefass, int tipoOrdenacao) throws Exception {
		String text = "NORMAL";
	
		if (tipoOrdenacao == VETOR_TAREFA_CRESCENTE) {
			text = "CRESCENTE";
		} else if (tipoOrdenacao == VETOR_TAREFA_DECRESCENTE) {
			text = "DECRESCENTE";
		}

		System.out.println("=============================================");
		System.out.println("# Carga Inicial: " + text);
		System.out.println("=============================================\n");

		for (int i = 0; i < vetCargaDasTarefass.length; i++) {
			System.out.println("Tarefa " + (i + 1) + ": " + vetCargaDasTarefass[i]);
		}
	
		System.out.println("");
	}

	/**
	 * C�digo respons�vel por executar o algoritmo de otimiza��o online List-scheduling. 
	 * */
	public static void listSchedluling(int qtdMaquinas, int qtdTarefas, int [] vetCargaDasTarefas) throws Exception {
		int [] vetExecucaoTarefaPorMaquina  = new int[qtdTarefas];
		int [] vetCargaTotalPorMaquina = new int [qtdMaquinas];
	
		PriorityQueue<Pair<Integer, Integer>> pq = inicializarPriorityQueue(qtdMaquinas);

		for (int i = 0; i < qtdTarefas; i++) {
			int cargaTarefa = vetCargaDasTarefas[i];
		
			//Pegando o primeiro elemento da fila e removendo-o
			Pair<Integer, Integer> pair = pq.poll();

			//Inserindo a carga na maquina 'i'
			vetCargaTotalPorMaquina[pair.getIdMaquina()] = pair.getCarga() + cargaTarefa;

			//Marcando em qual maquina a tarefa 'i' foi executada
			vetExecucaoTarefaPorMaquina[i] = pair.getIdMaquina();

			//Criando novo elemento com valor atualizado para ser inserido na fila de prioridade
			//Obs: o id da maquina nao eh alterado, apenas a sua carga
			Pair<Integer, Integer> newPair = new Pair<Integer, Integer>(vetCargaTotalPorMaquina[pair.getIdMaquina()], pair.getIdMaquina());
			pq.add(newPair);
		}

		printResult(vetExecucaoTarefaPorMaquina, vetCargaDasTarefas, vetCargaTotalPorMaquina, LIST_SCHEDULING, qtdMaquinas);
	}

	/**
	 * C�digo respons�vel por executar o algoritmo de otimiza��o online Random.
	 * */
	public static void random(int qtdMaquinas, int qtdTarefas, int [] vetCargaDasTarefas) throws Exception {
		int [] vetExecucaoTarefas  = new int[qtdTarefas];
		int [] vetCargaTotalPorMaquina = new int [qtdMaquinas];

		for (int i = 0; i < qtdTarefas; i++) {
			int cargaTarefa = vetCargaDasTarefas[i];
			int codMaquina = getMaquinaAleatoria(qtdMaquinas);

			vetCargaTotalPorMaquina[codMaquina] += cargaTarefa;
			vetExecucaoTarefas[i] = codMaquina;
		}

		printResult(vetExecucaoTarefas, vetCargaDasTarefas, vetCargaTotalPorMaquina, RANDOM, qtdMaquinas);
	}

	/**
	 * M�todo auxiliar para iniciar um fila de prioridade que ser� utilizada pelo algoritmo List-scheduling. Essa fila ser� utilizada
	 * para saber em quais m�quinas cada tarefa ser� executada. A carga inicial de todas as m�quinas � zero.
	 * */
	public static PriorityQueue<Pair<Integer, Integer>> inicializarPriorityQueue(int qtdMaquinas) throws Exception {
		PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<Pair<Integer, Integer>>();

		//Inicializando a fila de prioridade com peso 0 em todas as maquinas
		for (int i = 0; i < qtdMaquinas; i++) {
			pq.add(new Pair<Integer, Integer>(CARGA_INICIAL_DA_MAQUINA, i));
		}

		return pq;
	}

	/**
	 * Esse m�todo ser� respons�vel por printar todas as m�tricas, por algoritmo, desenvolvidas neste trabalho.
	 * 
	 * M�trica 1: Carga total por m�quina
	 * M�trica 2: Maior carga final
	 * M�trica 3: Soma de todas as cargas
	 * M�trica 4: Utilizacao m�dia
	 * M�trica 5: Maior carga por m�quina
	 * M�trica 6: Atraso por tarefa
	 * */
	public static void printResult(int [] vetExecucaoTarefa, int [] vetCargaDasTarefas, int [] vetCargaTotalPorMaquina, String nomeAlgoritmo, int qtdMaquinas) throws Exception {
		int maiorCargaFinal = 0;
		int codMaquinaMaiorCargaFinal = 0;
		int cargaTotal = 0;

		Map<Integer, ArrayList<Integer>> mapMaquinaPorTarefa = new HashMap<Integer, ArrayList<Integer>>();
		Map<Integer, ArrayList<Integer>> mapMaquinaPorCarga = new HashMap<Integer, ArrayList<Integer>>();

		Map<Integer, Integer> mapMaiorCargaPorMaquina = new HashMap<Integer, Integer>();

		System.out.println("=============================================");
		System.out.println("# Resultados do algoritmo: " + nomeAlgoritmo);
		System.out.println("=============================================\n");

		for (int i = 0; i < vetExecucaoTarefa.length; i++) {
			int codMaquina = vetExecucaoTarefa[i] + 1;
			int tarefa = (i + 1);
			int carga = vetCargaDasTarefas[i];

			StringBuilder sb = new StringBuilder();
			sb.append("Tarefa " + tarefa + ": ");
			sb.append("Carga " + carga + " -> ");
			sb.append("Executada na maquina " + codMaquina);

			System.out.println(sb.toString());

			populaMapMaquina(mapMaquinaPorTarefa, codMaquina, tarefa);
			populaMapMaquina(mapMaquinaPorCarga, codMaquina, carga);
		
			//Fazendo esse teste aqui para aproveitar o laco das tarefas executadas, assim ja consigo saber qual a maior carga por maquina
			if (!mapMaiorCargaPorMaquina.containsKey(codMaquina) || mapMaiorCargaPorMaquina.get(codMaquina) < carga) {
				mapMaiorCargaPorMaquina.put(codMaquina, carga);
			}
		}
		System.out.println("");

		System.out.println("=============================================");
		System.out.println("# Resultado Final");
		System.out.println("=============================================\n");

		System.out.println("=============================================");
		System.out.println("# Metrica 1: Carga total por m�quina");
		System.out.println("=============================================\n");
	
		for (int i = 0; i < vetCargaTotalPorMaquina.length; i++) {
			int codMaquina = i + 1;

			if (maiorCargaFinal == 0 || maiorCargaFinal < vetCargaTotalPorMaquina[i]) {
				maiorCargaFinal = vetCargaTotalPorMaquina[i];
				codMaquinaMaiorCargaFinal = codMaquina;
			}

			StringBuilder sb = new StringBuilder();
			sb.append("M�quina " + codMaquina + ": ");
			sb.append("Carga Total -> " + vetCargaTotalPorMaquina[i] + "\n");

			listarDadosPorMaquina(mapMaquinaPorTarefa, codMaquina, sb, "Tarefas");
			listarDadosPorMaquina(mapMaquinaPorCarga, codMaquina, sb, "Cargas");

			System.out.println(sb.toString());
		
			cargaTotal += vetCargaTotalPorMaquina[i];
		}
	
		System.out.println("=============================================");
		System.out.println("# Metrica 2: Maior carga final");
		System.out.println("=============================================\n");
	
		System.out.println("M�quina " + codMaquinaMaiorCargaFinal +  ": Carga -> " + maiorCargaFinal + "\n");
	
		System.out.println("=============================================");
		System.out.println("# Metrica 3: Soma de todas as cargas");
		System.out.println("=============================================\n");
	
		System.out.println("Total das cargas -> " + cargaTotal + "\n");
	
		System.out.println("=============================================");
		System.out.println("# Metrica 4: Utilizacao m�dia");
		System.out.println("=============================================\n");
	
		System.out.println("Media -> " + (double) cargaTotal / (maiorCargaFinal * qtdMaquinas) + "\n");

		System.out.println("=============================================");
		System.out.println("# Metrica 5: Maior carga por m�quina");
		System.out.println("=============================================\n");

		for (Map.Entry<Integer, Integer> map : mapMaiorCargaPorMaquina.entrySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append("M�quina " + map.getKey() + ": ");
			sb.append("Maior Carga -> " + map.getValue() + "\n");

			System.out.println(sb.toString());
		}

		System.out.println("=============================================");
		System.out.println("# Metrica 6: Atraso por tarefa");
		System.out.println("=============================================\n");

		for (int i = 0; i < vetCargaTotalPorMaquina.length; i++) {
			int codMaquina = i + 1;

			List<Integer> listTarefas = mapMaquinaPorTarefa.get(codMaquina);
			if (listTarefas == null) {
				throw new Exception("Falha ao buscar as tarefas executadas por m�quina.");
			}

			List<Integer> listCargas = mapMaquinaPorCarga.get(codMaquina);
			if (listCargas == null) {
				throw new Exception("Falha ao buscar as cargas executadas por m�quina.");
			}

			String listaTarefasexec = "";
			int qtdAtrasoPorTarefa = 0;

			StringBuilder sb = new StringBuilder();
			sb.append("M�quina " + codMaquina + ":\n");

			for (int j = 0; j < listTarefas.size(); j++) {
				int tarefa = listTarefas.get(j);
				int carga = listCargas.get(j);

				String texto = listaTarefasexec.isEmpty() ? "(Primeira Execu��o)" : "(Esperou tarefa(s): " + listaTarefasexec + ")";
				sb.append("Tarefa " + tarefa + ": " + qtdAtrasoPorTarefa + " " + texto + "\n");

				if (listaTarefasexec.isEmpty()) {
					listaTarefasexec += tarefa;
				} else {
					listaTarefasexec += ", " + tarefa;
				}

				qtdAtrasoPorTarefa += carga;
			}

			System.out.println(sb.toString());
		}
	}

	/**
	 * M�tod auxiliar respons�vel por popular uma map para que a mesma possa servir de insumo para printar os resultados da m�trica 1:
	 * Carga total por m�quina.
	 * Chave nesse metodo � um conceito: em um momento pode ser 'Tarefa' e em outro 'Carga'.
	 * */
	public static void populaMapMaquina(Map<Integer, ArrayList<Integer>> mapMaquinaPorChave, int codMaquina, int chave) throws Exception {
		if (mapMaquinaPorChave.containsKey(codMaquina)) {
			ArrayList<Integer> listChave = mapMaquinaPorChave.get(codMaquina);
			listChave.add(chave);
		} else {
			ArrayList<Integer> listaTarefas = new ArrayList<Integer>();
			listaTarefas.add(chave);

			mapMaquinaPorChave.put(codMaquina, listaTarefas);
		}
	}

	/**
	 * M�todo auxiliar respons�vel ler os dados de uma map e popular os dados das tarefas e cargas executadas em um StringBuilder 
	 * para que eles possam ser printados na m�trica 1: Carga total por m�quina.
	 * Chave nesse m�todo � um conceito: em um momento pode ser 'Tarefa' e em outro 'Carga'.
	 * */
	public static void listarDadosPorMaquina(Map<Integer, ArrayList<Integer>> mapMaquinaPorChave, int codMaquina, StringBuilder sb, String textoChave) throws Exception {
		sb.append(textoChave + ": ");

		if (mapMaquinaPorChave.containsKey(codMaquina)) {
			ArrayList<Integer> listaChave = mapMaquinaPorChave.get(codMaquina);
		
			sb.append("[ ");
			for (int j = 0; j < listaChave.size(); j++) {
				sb.append(listaChave.get(j));

				if (j != listaChave.size() - 1) {
					sb.append(", ");
				}
			}
		
			sb.append(" ]\n");
		} else {
			throw new Exception("Falha ao buscar as " + textoChave + " executadas por m�quina.");
		}
	}

	/**
	 * M�todo auxiliar do algoritmo de otimiza��o online Random. Este m�todo ir� devoler o c�digo de uma m�quina aleat�ria para que 
	 * a tarefa possa ser executada na mesma.
	 * */
	private static int getMaquinaAleatoria(int qtdMaquinas) throws Exception {
		int codMaquina = (int) (Math.random() * qtdMaquinas);
	
		if (codMaquina < 0 || codMaquina > qtdMaquinas) {
			throw new Exception("Nao foi possivel encontrar um codigo de maquina entre 1 e " + qtdMaquinas + ". Codigo encontrado: " + codMaquina);
		}

		return codMaquina;
	}

	/**
	 * M�todo de teste: utilizado durante a implementa��o do trabalho.
	 * */
	public static int[] getCargaFixa(int qtdTarefas) throws Exception {
		int [] vetCargaTarefa = new int[qtdTarefas];

		vetCargaTarefa[0] = 76;
		vetCargaTarefa[1] = 72;
		vetCargaTarefa[2] = 22;
		vetCargaTarefa[3] = 86;
		vetCargaTarefa[4] = 81;

		return vetCargaTarefa;
	}

	/**
	 * M�todo de teste: utilizado durante a implementa��o do trabalho.
	 * */
	private static void printVetor(int[] vetCargaDasTarefas) throws Exception {
		for (int i = 0; i < vetCargaDasTarefas.length; i++) {
			System.out.println("Tarefa " + (i + 1) + ": " + vetCargaDasTarefas[i]);
		}
	}
}