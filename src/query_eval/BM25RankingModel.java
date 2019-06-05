package query_eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indice.estrutura.Ocorrencia;
import static query_eval.UtilQuery.getOrderedList;

public class BM25RankingModel implements RankingModel 
{
	private IndicePreCompModelo idxPrecompVals;
	private double b;
	private int k1;
	
	public BM25RankingModel(IndicePreCompModelo idxPrecomp,double b,int k1)
	{
		this.idxPrecompVals = idxPrecomp;
		this.b = b;
		this.k1 = k1;
	}
	/**
	 * Calcula o idf (adaptado) do bm25
	 * @param numDocs
	 * @param numDocsArticle
	 * @return
	 */
	public double idf(int numDocs,int numDocsArticle)
	{
		return Math.log10((numDocs - numDocsArticle + 0.5)/(numDocsArticle + 0.5));
	}
	/**
	 * Calcula o beta_{i,j}
	 * @param freqTerm
	 * @return
	 */
	public double beta_ij(int freqTermDoc, int docId) {
		return ((k1 + 1) * freqTermDoc)/(k1*((1-b) + (b*(idxPrecompVals.getDocumentLength(docId)/idxPrecompVals.getAvgLenPerDocument()))) + freqTermDoc);
	}
	
	/**
	 * Retorna a lista ordenada de documentos usando o modelo do BM25.
	 * para isso, em dj_weight calcule o peso do documento j para a consulta. 
	 * Para cada termo, calcule o Beta_{i,j} e o idf e acumule o pesso desse termo para o documento. 
	 * Logo após, utilize UtilQuery.getOrderedList para ordenar e retornar os docs ordenado
	 * mapQueryOcour: Lista de ocorrencia de termos na consulta
	 * lstOcorrPorTermoDocs: Lista de ocorrencia dos termos nos documentos (apenas termos que ocorrem na consulta)
	 */
	@Override
	public List<Integer> getOrderedDocs(Map<String, Ocorrencia> mapQueryOcur,
			Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs) {
		
		
		Map<Integer,Double> dj_weight = new HashMap<Integer,Double>();
                double Idf, b_ij;
		
		for(Map.Entry<String, Ocorrencia> termo : mapQueryOcur.entrySet())
                {
                    int numDocs = idxPrecompVals.getNumDocumentos();                 
                    int freqTerm = termo.getValue().getFreq();
                    List<Ocorrencia> l = lstOcorrPorTermoDocs.get(termo.getKey());
                    int numDocsTerm = l.size();
                                                           
                    Idf = idf(numDocs, numDocsTerm);
                    
                    for(Ocorrencia o : l){
                        b_ij = beta_ij(freqTerm, o.getDocId());
                        dj_weight.put(o.getDocId(), dj_weight.get(termo) + (b_ij * Idf));
                        idxPrecompVals.updateSumSquaredForNorm(numDocsTerm, o);
                    }
                }
                
                
                return getOrderedList(dj_weight);
                
	}
	

}
