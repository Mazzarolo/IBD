package ibd.query.binaryop;

import ibd.query.Operation;
import ibd.query.Tuple;

public class Difference extends BinaryOperation {

    Tuple atualEsq;
    Tuple proxEsq;
    Tuple atualDir;

    public Difference(Operation op1, Operation op2) throws Exception {
        super(op1, op2);
    }

    @Override
    public void open() throws Exception {
        super.open();
        atualEsq = null;
        proxEsq = null;
        atualDir = null;
    }

    @Override
    public Tuple next() throws Exception {
        if (proxEsq != null) {
            Tuple prox = proxEsq;
            proxEsq = null;
            return prox;
        }

        return diff();
    }

    @Override
    public boolean hasNext() throws Exception {
        if (proxEsq != null) {
            return true;
        }

        proxEsq = diff();
        return proxEsq != null;
    }

    private Tuple diff() throws Exception {
        while (atualEsq != null || op1.hasNext()) { // Verifica se há um valor no ponteiro da tabela esquerda ou se ainda é possível avançá-lo
            if (atualEsq == null) {
                atualEsq = op1.next(); // Caso não haja valor no ponteiro, ele recebe a próxima tupla da tabela esquerda
            }
            int compara = 1; // Flag para saber a relação entre a chave da esquerda e a chave da direita
            while ((op2.hasNext() || atualDir != null) && compara != 0) {
                if (atualDir == null) {
                    atualDir = op2.next();
                }
                Long chave1 = atualEsq.sourceTuples[tupleIndex1].record.getPrimaryKey();
                Long chave2 = atualDir.sourceTuples[tupleIndex1].record.getPrimaryKey();

                compara = Long.compare(chave1, chave2); // Retorna a relação entre as chaves primárias das tuplas atuais

                if (compara < 0) { 				// Se a comparação retornar -1, significa que a chave da direita é maior que a da esquerda
                    Tuple r = new Tuple();		// e como as tabelas estão ordenadas, isso significa que a chave da esquerda NÃO existe
                    r.addSource(atualEsq);		// na tabela da direita, portanto, a atual tupla no ponteira da esquerda é retornada no resultado
                    atualEsq = null;
                    return r;
                }
                atualDir = null; // Se a comapração tiver retornado 0 ou 1, significa que o valor da esqeurda é igual ou maior que o da direita, portanto o ponteiro da direita é avançado
            }
            if (compara != 0) { // Caso o laço anterior tenha sido encerrado/pulado por não haver mais tuplas na tabela da direita e ainda houver pelo menos uma tupla restante na tabela da esquerda, ela deve ser adicionada ao resultado
                Tuple r = new Tuple();
                r.addSource(atualEsq);
                atualEsq = null;
                return r;
            }
            atualEsq = null; // O ponteiro da esquerda é zerado para que o algoritmo possa avançá-lo na próxima chamada
        }
        return null; // Caso não haja um tupla na tabela esquerda e nem tuplas restantes, retorna nada
    }

    @Override
    public String toString() {
        return "Difference";
    }

}
