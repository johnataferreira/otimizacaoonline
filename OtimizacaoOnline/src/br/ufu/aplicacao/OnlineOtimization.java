package br.ufu.aplicacao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
	public static final int		QTD_TAREFAS_DEFAULT 		= 128;
	public static final int		QTD_TAREFAS_TESTE 			= 10;
	//public static final int		QTD_MAQUINAS_DEFAULT 		= 10;

	public static final int		CARGA_INICIAL_DA_MAQUINA 	= 0;

	public static final String 	LIST_SCHEDULING 			= "List-scheduling";
	public static final String 	RANDOM 						= "Random";
	public static final String 	ROUND_ROBIN 				= "Round-robin";

	public static final int 	VETOR_TAREFA_CRESCENTE		= 1;
	public static final int 	VETOR_TAREFA_NORMAL			= 0;
	public static final int 	VETOR_TAREFA_DECRESCENTE	= -1;

	public static final String 	PATH						= "E:\\Johnata_Arquivos\\UFU\\7� Per�odo\\TCC I\\ArquivosGeradosAPP";
	public static final String 	PATH_ATRASO					= PATH + "\\BoxPlotAtraso";
	public static final String 	PATH_CARGA_TOTAL			= PATH + "\\BoxPlotCargaTotalPorMaquina";

	public static final String LN							= "\n";

	public static BufferedWriter writerResultado 					= null;

	public static void main(String[] args) {
		try {
			//int qtdTarefas = getValueFromArgs(args, 0, QTD_TAREFAS_DEFAULT);
			//int qtdMaquinas = getValueFromArgs(args, 1, QTD_MAQUINAS_DEFAULT);
		
			//int qtdTarefas = gerarQuantidadeAleatorias(100, 1000);
			//int qtdMaquinas = gerarQuantidadeAleatorias(5, 50);
			deleteFiles();
			
			writerResultado = criarBufferedWriter("ResultadoGeral.txt", PATH);

			int qtdTarefas = QTD_TAREFAS_TESTE; //TODO: Colocar essa cara como default QTD_TAREFAS_DEFAULT;
			int [] vetCargaDasTarefasBase = getCargaInicial(qtdTarefas);

			//A execu��o ser� feita considerando 8, 16 e 32 m�quinas
			//TODO: rancar esse for e colocar o de baixo, com 8, 16 e 32 maquinas
			for (int qtdMaquinas = 2; qtdMaquinas <= 8; qtdMaquinas = qtdMaquinas * 2) {
			//for (int qtdMaquinas = 8; qtdMaquinas <= 32; qtdMaquinas = qtdMaquinas * 2) {
				int [] vetCargaDasTarefasClonado = cloneVetor(vetCargaDasTarefasBase);
				
				execute(qtdMaquinas, qtdTarefas, vetCargaDasTarefasClonado, VETOR_TAREFA_NORMAL);
				execute(qtdMaquinas, qtdTarefas, vetCargaDasTarefasClonado, VETOR_TAREFA_CRESCENTE);
				execute(qtdMaquinas, qtdTarefas, vetCargaDasTarefasClonado, VETOR_TAREFA_DECRESCENTE);
			}
		} catch (Exception e) {
			System.out.println("Erro inesperado: " + e.getMessage());
			e.printStackTrace();

			//Se houver qualquer erro temos que fechar o writer do resultado geral antes de deletar os arquivos.
			try {
				closeBufferedWriter(writerResultado);
			} catch (Exception ignored) {
			} finally {
				writerResultado = null;
			}

			deleteFiles();
		} finally {
			try {
				closeBufferedWriter(writerResultado);
			} catch (Exception ignored) {
			}
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
	 * M�todo respons�vel por retornar um vetor de inteiros com valores aleat�rios da carga de tarefas entre 100 e 1000.
	 * A quantidade de valores ser� passada como par�metros.
	 * */
	public static int[] getCargaInicial(int qtdTarefas) throws Exception {
		int [] vetCargaDasTarefas = new int[qtdTarefas];

		for (int i = 0; i < qtdTarefas; i++) {
			//Gerando carga de tarefa entre 100 e 1000
			vetCargaDasTarefas[i] = (int) (Math.random() * 900) + 100;
		}
	
		return vetCargaDasTarefas;
	}

	/**
	 * M�todo respons�vel por fazer uma c�pia do vetor inicial das tarefas, pois n�o podemos alter�-lo. 
	 * =Ele ser� utilizado como base para todas as execu��es. 
	 * */
	private static int[] cloneVetor(int[] vetCargaDasTarefasBase) throws Exception {
		int [] vetCargaDasTarefasClonado = new int[vetCargaDasTarefasBase.length];

		for (int i = 0; i < vetCargaDasTarefasBase.length; i++) {
			vetCargaDasTarefasClonado[i] = vetCargaDasTarefasBase[i];
		}

		return vetCargaDasTarefasClonado;
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

		listSchedluling(qtdMaquinas, qtdTarefas, vetCargaDasTarefas, tipoOrdenacao);
		random(qtdMaquinas, qtdTarefas, vetCargaDasTarefas, tipoOrdenacao);
		roundRobin(qtdMaquinas, qtdTarefas, vetCargaDasTarefas, tipoOrdenacao);
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
		StringBuilder sbResult = new StringBuilder();

		if (tipoOrdenacao == VETOR_TAREFA_CRESCENTE) {
			text = "CRESCENTE";
		} else if (tipoOrdenacao == VETOR_TAREFA_DECRESCENTE) {
			text = "DECRESCENTE";
		}

		sbResult.append("=============================================" + LN);
		sbResult.append("# Carga Inicial: " + text + LN);
		sbResult.append("=============================================\n" + LN);

		for (int i = 0; i < vetCargaDasTarefass.length; i++) {
			sbResult.append("Tarefa " + (i + 1) + ": " + vetCargaDasTarefass[i] + LN);
		}

		write(writerResultado, sbResult);
	}

	/**
	 * C�digo respons�vel por executar o algoritmo de otimiza��o online List-scheduling. 
	 * */
	public static void listSchedluling(int qtdMaquinas, int qtdTarefas, int [] vetCargaDasTarefas, int tipoOrdenacao) throws Exception {
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

		printResult(vetExecucaoTarefaPorMaquina, vetCargaDasTarefas, vetCargaTotalPorMaquina, LIST_SCHEDULING, qtdMaquinas, tipoOrdenacao);
	}

	/**
	 * C�digo respons�vel por executar o algoritmo de otimiza��o online Random.
	 * */
	public static void random(int qtdMaquinas, int qtdTarefas, int [] vetCargaDasTarefas, int tipoOrdenacao) throws Exception {
		int [] vetExecucaoTarefas  = new int[qtdTarefas];
		int [] vetCargaTotalPorMaquina = new int [qtdMaquinas];

		for (int i = 0; i < qtdTarefas; i++) {
			int cargaTarefa = vetCargaDasTarefas[i];
			int codMaquina = getMaquinaAleatoria(qtdMaquinas);

			vetCargaTotalPorMaquina[codMaquina] += cargaTarefa;
			vetExecucaoTarefas[i] = codMaquina;
		}

		printResult(vetExecucaoTarefas, vetCargaDasTarefas, vetCargaTotalPorMaquina, RANDOM, qtdMaquinas, tipoOrdenacao);
	}

	/**
	 * C�digo respons�vel por executar o algoritmo de otimiza��o online Round-robin.
	 * */
	private static void roundRobin(int qtdMaquinas, int qtdTarefas, int[] vetCargaDasTarefas, int tipoOrdenacao) throws Exception {
		int [] vetExecucaoTarefas  = new int[qtdTarefas];
		int [] vetCargaTotalPorMaquina = new int [qtdMaquinas];

		int codMaquina = 0;
		boolean isPrimeiraExecucao = true;

		for (int i = 0; i < qtdTarefas; i++) {
			int cargaTarefa = vetCargaDasTarefas[i];
			
			if (!isPrimeiraExecucao) {
				codMaquina = getMaquinaRoundRobin(codMaquina, qtdMaquinas);
			}
			isPrimeiraExecucao = false;

			vetCargaTotalPorMaquina[codMaquina] += cargaTarefa;
			vetExecucaoTarefas[i] = codMaquina;
		}

		printResult(vetExecucaoTarefas, vetCargaDasTarefas, vetCargaTotalPorMaquina, ROUND_ROBIN, qtdMaquinas, tipoOrdenacao);
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
	 * M�trica 6: Carga m�dia de execu��o por m�quina
	 * M�trica 7: Atraso por tarefa
	 * */
	public static void printResult(int [] vetExecucaoTarefa, int [] vetCargaDasTarefas, int [] vetCargaTotalPorMaquina, String nomeAlgoritmo, int qtdMaquinas, int tipoOrdenacao) throws Exception {
		int maiorCargaFinal = 0;
		int codMaquinaMaiorCargaFinal = 0;
		int cargaTotal = 0;

		Map<Integer, ArrayList<Integer>> mapMaquinaPorTarefa = new HashMap<Integer, ArrayList<Integer>>();
		Map<Integer, ArrayList<Integer>> mapMaquinaPorCarga = new HashMap<Integer, ArrayList<Integer>>();

		Map<Integer, Integer> mapMaiorCargaPorMaquina = new HashMap<Integer, Integer>();

		BufferedWriter writerCargaTotalPorMaquina = null;
		BufferedWriter writerAtraso = null;

		try {
			writerCargaTotalPorMaquina = criarBufferedWriter(getFileName(nomeAlgoritmo, qtdMaquinas, tipoOrdenacao), PATH_CARGA_TOTAL);
			writerAtraso = criarBufferedWriter(getFileName(nomeAlgoritmo, qtdMaquinas, tipoOrdenacao), PATH_ATRASO);

			StringBuilder sbResult = new StringBuilder();

			sbResult.append("===================================================" + LN);
			sbResult.append("# Resultados do algoritmo: " + nomeAlgoritmo + LN);
			sbResult.append("===================================================\n");

			write(writerResultado, sbResult);

			int maquinaCorrenteEsperada = 1;

			for (int i = 0; i < vetExecucaoTarefa.length; i++) {
				int codMaquina = vetExecucaoTarefa[i] + 1;
				
				//Se a maquina esperada n�o estiver sendo executada neste momento significa
				//que ela n�o executar� nenhuma tarefa. Assim, colocamos ela na map de maior 
				//carga por tarefa: "mapMaiorCargaPorMaquina", para que ele seja printada no 
				//resultado, por�m sem carga.
				if (maquinaCorrenteEsperada != codMaquina) {
					for (int j = maquinaCorrenteEsperada; j < codMaquina; j++) {
						mapMaiorCargaPorMaquina.put(j, 0);
					}
				}
				
				int tarefa = (i + 1);
				int carga = vetCargaDasTarefas[i];

				sbResult.append("Tarefa " + tarefa + ": ");
				sbResult.append("Carga " + carga + " -> ");
				sbResult.append("Executada na maquina " + codMaquina);

				write(writerResultado, sbResult);

				populaMapMaquina(mapMaquinaPorTarefa, codMaquina, tarefa);
				populaMapMaquina(mapMaquinaPorCarga, codMaquina, carga);
			
				//Fazendo esse teste aqui para aproveitar o laco das tarefas executadas, assim ja consigo saber qual a maior carga por maquina
				if (!mapMaiorCargaPorMaquina.containsKey(codMaquina) || mapMaiorCargaPorMaquina.get(codMaquina) < carga) {
					mapMaiorCargaPorMaquina.put(codMaquina, carga);
				}

				maquinaCorrenteEsperada++;
			}
			sbResult.append("" + LN);

			sbResult.append("===================================================" + LN);
			sbResult.append("# Resultado Final" + LN);
			sbResult.append("===================================================\n" + LN);

			sbResult.append("===================================================" + LN);
			sbResult.append("# Metrica 1: Carga total por m�quina" + LN);
			sbResult.append("===================================================\n");

			write(writerResultado, sbResult);
		
			for (int i = 0; i < vetCargaTotalPorMaquina.length; i++) {
				int codMaquina = i + 1;

				if (maiorCargaFinal == 0 || maiorCargaFinal < vetCargaTotalPorMaquina[i]) {
					maiorCargaFinal = vetCargaTotalPorMaquina[i];
					codMaquinaMaiorCargaFinal = codMaquina;
				}

				sbResult.append("M�quina " + codMaquina + ": ");
				sbResult.append("Carga Total -> " + vetCargaTotalPorMaquina[i] + "\n");

				listarDadosPorMaquina(mapMaquinaPorTarefa, codMaquina, sbResult, "Tarefas");
				listarDadosPorMaquina(mapMaquinaPorCarga, codMaquina, sbResult, "Cargas");

				write(writerResultado, sbResult);

				cargaTotal += vetCargaTotalPorMaquina[i];

				writeCargaTotal(writerCargaTotalPorMaquina, codMaquina, vetCargaTotalPorMaquina[i]);
			}

			sbResult.append("===================================================" + LN);
			sbResult.append("# Metrica 2: Maior carga final" + LN);
			sbResult.append("===================================================\n" + LN);
		
			sbResult.append("M�quina " + codMaquinaMaiorCargaFinal +  ": Carga -> " + maiorCargaFinal + "\n" + LN);
		
			sbResult.append("===================================================" + LN);
			sbResult.append("# Metrica 3: Soma de todas as cargas" + LN);
			sbResult.append("===================================================\n" + LN);
		
			sbResult.append("Total das cargas -> " + cargaTotal + "\n" + LN);
		
			sbResult.append("===================================================" + LN);
			sbResult.append("# Metrica 4: Utilizacao m�dia" + LN);
			sbResult.append("===================================================\n" + LN);
		
			sbResult.append("Media -> " + (double) cargaTotal / (maiorCargaFinal * qtdMaquinas) + "\n" + LN);

			sbResult.append("===================================================" + LN);
			sbResult.append("# Metrica 5: Maior carga por m�quina" + LN);
			sbResult.append("===================================================\n");

			write(writerResultado, sbResult);

			for (Map.Entry<Integer, Integer> map : mapMaiorCargaPorMaquina.entrySet()) {
				sbResult.append("M�quina " + map.getKey() + ": ");
				sbResult.append("Maior Carga -> " + map.getValue() + "\n");

				write(writerResultado, sbResult);
			}

			sbResult.append("===================================================" + LN);
			sbResult.append("# Metrica 6: Carga m�dia de execu��o por m�quina" + LN);
			sbResult.append("===================================================\n" + LN);

			sbResult.append("Media -> " + (double) cargaTotal / qtdMaquinas + "\n" + LN);

			sbResult.append("===================================================" + LN);
			sbResult.append("# Metrica 7: Atraso por tarefa" + LN);
			sbResult.append("===================================================\n" + LN);

			for (int i = 0; i < vetCargaTotalPorMaquina.length; i++) {
				int codMaquina = i + 1;

				List<Integer> listTarefas = mapMaquinaPorTarefa.get(codMaquina);
				List<Integer> listCargas = mapMaquinaPorCarga.get(codMaquina);

				if (listTarefas == null || listCargas == null) {
					sbResult.append("M�quina " + codMaquina + ":\n");
					sbResult.append("Sem Tarefas!\n");

					write(writerResultado, sbResult);
					continue;
				}

				String listaTarefasexec = "";
				int qtdAtrasoPorTarefa = 0;

				sbResult.append("M�quina " + codMaquina + ":\n");

				for (int j = 0; j < listTarefas.size(); j++) {
					int tarefa = listTarefas.get(j);
					int carga = listCargas.get(j);

					String texto = listaTarefasexec.isEmpty() ? "(Primeira Execu��o)" : "(Esperou tarefa(s): " + listaTarefasexec + ")";
					sbResult.append("Tarefa " + tarefa + ": " + qtdAtrasoPorTarefa + " " + texto + "\n");

					writeAtraso(writerAtraso, tarefa, qtdAtrasoPorTarefa);

					if (listaTarefasexec.isEmpty()) {
						listaTarefasexec += tarefa;
					} else {
						listaTarefasexec += ", " + tarefa;
					}

					qtdAtrasoPorTarefa += carga;
				}

				write(writerResultado, sbResult);
			}
		} finally {
			try {
				closeBufferedWriter(writerCargaTotalPorMaquina);
				closeBufferedWriter(writerAtraso);
			} catch (Exception e2) {
				System.out.println("Erro inesperado ao descarregar no arquivo: " + e2.getMessage());
				e2.printStackTrace();
			}
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
			sb.append("[ ");
			sb.append(" ]\n");
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
	 * M�todo auxiliar do algoritmo de otimiza��o online Round-robin. Este m�todo ir� devoler o c�digo de uma m�quina para que 
	 * a tarefa possa ser executada na mesma, considerando a l�gica circular deste algoritmo.
	 * */
	private static int getMaquinaRoundRobin(int codMaquina, int qtdMaquinas) throws Exception {
		int proximaMaquina = 0;

		if ((codMaquina + 1) < qtdMaquinas) {
			proximaMaquina = codMaquina + 1;
		}

		return proximaMaquina;
	}

	/**
	 * M�todo respons�vel por criar o BufferedWriter.
	 * */
	public static BufferedWriter criarBufferedWriter(String fileName, String path) throws Exception {
		File file = new File(path);

		if (!file.exists()) {
			file.mkdir();
		}

		return new BufferedWriter(new FileWriter(new File(path, fileName)));
	}
	
	/**
	 * M�todo respons�vel por fechar o BufferedWriter.
	 * */
	public static void closeBufferedWriter(BufferedWriter writer) throws Exception {
		if (writer != null) {
			writer.flush();
			writer.close();
		}
	}

	/**
	 * M�todo respons�vel por montar o nome do arquivo de acordo com os par�metros: tipo do algoritmo, tipo da ordena��o, quantidade de 
	 * m�quinas.
	 * */
	public static String getFileName(String nomeAlgoritmo, int qtdMaquinas, int tipoOrdenacao) throws Exception {
		return nomeAlgoritmo + "_" + qtdMaquinas + "_Maquinas_Ordenacao_" + getNomeOrdenacao(tipoOrdenacao) + ".txt";
	}

	/**
	 * M�todo respons�vel por pegar o nome da ordena��o de acordo com o tipo passado como par�metro.
	 * */
	public static String getNomeOrdenacao(int tipoOrdenacao) throws Exception {
		String nomeOrdenacao = "";
		
		if (tipoOrdenacao == VETOR_TAREFA_NORMAL) {
			nomeOrdenacao = "1_Normal";

		} else if (tipoOrdenacao == VETOR_TAREFA_CRESCENTE) {
			nomeOrdenacao = "2_Crescente";

		} else if (tipoOrdenacao == VETOR_TAREFA_DECRESCENTE) {
			nomeOrdenacao = "3_Decrescente";

		} else {
			throw new Exception("Tipo de ordena��o informado inv�lido: " + tipoOrdenacao + ".");
		}
		
		return nomeOrdenacao;
	}

	/**
	 * M�todo respons�vel por descarregar o StringBuilder no arquivo.
	 * */
	public static void write(BufferedWriter writer, StringBuilder sb) throws Exception {
		System.out.println(sb.toString());

		if (writer != null) {
			writer.write(sb.toString() + LN);
			sb.setLength(0);
		}
	}

	/**
	 * M�todo respons�vel por descarregar o atraso no arquivo.
	 * */
	public static void writeAtraso(BufferedWriter writerAtraso, int tarefa, int qtdAtrasoPorTarefa) throws Exception {
		writerAtraso.write(tarefa + " - " + qtdAtrasoPorTarefa + "\n");
	}

	/**
	 * M�todo respons�vel por descarregar a carga total no arquivo.
	 * */
	private static void writeCargaTotal(BufferedWriter writerCargaTotalPorMaquina, int codMaquina, int cargaTotalPorMaquina) throws Exception {
		writerCargaTotalPorMaquina.write(codMaquina + " - " + cargaTotalPorMaquina + "\n");
	}

	/**
	 * M�todo respons�vel por deletar os arquivos gerados se qualquer erro ocorrer ou no inicio de cada gera��o.
	 * */
	public static void deleteFiles() {
		try {
			File file = new File(PATH);
			File [] files = file.listFiles();

			for (int i = 0; i < files.length; i++) {
				File arquivo = files[i];

				if (arquivo.exists()) {
					if (arquivo.isDirectory()) {
						File [] arquivos = arquivo.listFiles();
					
						for (int j = 0; j < arquivos.length; j++) {
							arquivos[j].delete();
						}
					}

					arquivo.delete();		
				}
			}
		} catch (Exception ignored) {
		}
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