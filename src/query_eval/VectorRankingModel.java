package query_eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indice.estrutura.Ocorrencia;
import static query_eval.UtilQuery.getOrderedList;


public class VectorRankingModel implements RankingModel 
{


	private IndicePreCompModelo idxPrecompVals;
	
	
	public static double tf(int freqTerm)
	{
		if(freqTerm > 0)
                    return 1 + Math.log10(freqTerm);
                else
                    return 0;
	}
	public static double idf(int numDocs,int numDocsTerm)
	{
            return Math.log10(numDocs/numDocsTerm);
	}
	public static double tfIdf(int numDocs,int freqTerm,int numDocsTerm)
	{
            return tf(freqTerm) * idf(numDocs,numDocsTerm);
	}
	public VectorRankingModel(IndicePreCompModelo idxPrecomp)
	{
		this.idxPrecompVals = idxPrecomp;
	}

	/**
	 * Retorna uma lista ordenada de documentos usando o tf xidf.
	 * Para isso, para cada termo da consulta, calcule o w_iq (peso do termo i na consulta q) e o w_ij e, logo apos, acumule 
	 * o w_iq x w_ij no peso do documento (dj_weight). Depois de ter o peso de cada documento, 
	 * divida pela norma do documento (use o idxPrecompVals.getNormaDocumento)
	 * Apos ter o peso para cada documento, rode o UtilQuery.getOrderedList para retornar a lista de documentos
	 * ordenados pela consulta
	 */
	@Override
	public List<Integer> getOrderedDocs(Map<String, Ocorrencia> mapQueryOcur,
			Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs) {
		
		Map<Integer,Double> dj_weight = new HashMap<Integer,Double>();
		
                //for each termo in mapQueryOcur
                //  calcula w_iq
                //  for each documento in lstOcorrPorTermoDocs
                //      calcula w_ij
                //      dj_weight = wij * w_iq
                double w_iq, w_ij;

                
                for(Map.Entry<String, Ocorrencia> termo : mapQueryOcur.entrySet())
                {
                    int numDocs = idxPrecompVals.getNumDocumentos();                 
                    int freqTerm = termo.getValue().getFreq();
                    List<Ocorrencia> l = lstOcorrPorTermoDocs.get(termo.getKey());
                    int numDocsTerm = l.size();
                                                           
                    w_iq = tfIdf(numDocs, freqTerm, numDocsTerm);
                    
                    for(Ocorrencia o : l){
                        w_ij = tfIdf(numDocs, o.getFreq(), l.size());
                        dj_weight.put(o.getDocId(), dj_weight.get(termo) + (w_iq * w_ij));
                        idxPrecompVals.updateSumSquaredForNorm(numDocsTerm, o);
                    }
                }
                
                
                return getOrderedList(dj_weight);
                
                //for each doc in dj_weight
                //  normaliza doc
                
                //getOrderedList(dj_weight)
                //return dj_weight

	}
	
	
}

