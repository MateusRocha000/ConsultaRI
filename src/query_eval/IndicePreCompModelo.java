package query_eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indice.estrutura.Indice;
import indice.estrutura.Ocorrencia;




public class IndicePreCompModelo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int numDocumentos = 0;
	private double avgLenPerDocument = 0;
	private Map<Integer,Integer> tamPorDocumento = new HashMap<Integer,Integer>();
	private Map<Integer,Double> normaPorDocumento = new HashMap<Integer,Double>();
	
	
	
	private Indice idx;
	
	public IndicePreCompModelo(Indice idx)
	{
		this.idx = idx;
		
		precomputeValues(idx);
		
		
	}
	/**
	 * Acumula o (tfxidf)^2 de mais uma ocorrencia (oc) no somatorio para calcular a norma por documento 
	 * Usar a propria norma para acumular o somatorio
	 * @param lstOcc
	 * @param oc
	 */
	public void updateSumSquaredForNorm(int numDocsTerm, Ocorrencia oc) {
                double tf = (oc.getFreq() > 0) ? 1 + Math.log10(oc.getFreq()) : 0;
                double idf = Math.log10(idx.getNumDocumentos()/numDocsTerm);
		
                normaPorDocumento.put(oc.getDocId(), normaPorDocumento.get(oc.getDocId()) + Math.pow(tf*idf, 2));
                
	}
	/**
	 * Atualiza o tamPorDocumento com mais uma ocorrencia 
	 * @param oc
	 */
	public void updateDocTam(Ocorrencia oc) {
                
	}
	/**
	 * Inicializa os atributos por meio do indice (idx):
	 * numDocumentos: o numero de documentos que o indice possui
	 * avgLenPerDocument: média do tamanho (em palavras) dos documentos
	 * tamPorDocumento: para cada doc id, seu tamanho (em palavras) - use o metodo updateDocTam para auxiliar
	 * normaPorDocumento: A norma por documento (cada termo é representado pelo seu peso (tfxidf) - use o metodo updateSumSquaredForNorm para auxiliar
	 * @param idx
	 */
	private void precomputeValues(Indice idx) {
            
            //Inicializa numDocumentos
            numDocumentos = idx.getNumDocumentos();
            
            //Inicializa avgLenPerDocument
            
            
            //Inicializa tamPorDocumento
            
                        
            //Inicializa normaPorDocumento
            for(String term : idx.getListTermos())
            {
                for(Ocorrencia l : idx.getListOccur(term))
                {
                    updateSumSquaredForNorm(idx.getNumDocPerTerm().get(term), l);
                    Math.sqrt(normaPorDocumento.get(l.getDocId()));
                }
            }
	}


	public int getDocumentLength(int docId)
	{
		return this.tamPorDocumento.get(docId);
	}
	public int getNumDocumentos() {
		return numDocumentos;
	}

	public void setNumDocumentos(int numDocumentos) {
		this.numDocumentos = numDocumentos;
	}

	public double getAvgLenPerDocument() {
		return avgLenPerDocument;
	}

	public void setAvgLenPerDocument(double avgLenPerDocument) {
		this.avgLenPerDocument = avgLenPerDocument;
	}

	public Map<Integer, Double> getNormaPorDocumento() {
		return normaPorDocumento;
	}

	public void setNormaPorDocumento(Map<Integer, Double> normaPorDocumento) {
		this.normaPorDocumento = normaPorDocumento;
	}

	public double getNormaDocumento(int docId)
	{
		return this.normaPorDocumento.get(docId);
	}
	
}

