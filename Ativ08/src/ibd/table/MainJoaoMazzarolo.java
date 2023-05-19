package ibd.table;

public class MainJoaoMazzarolo {

    public void addAluno(Table table, long pk, String nome, int matricula) throws Exception {
        table.addRecord(pk, buildAluno(matricula, nome));
    }

    public String buildAluno(int matricula, String nome) {
        return ("{matricula: " + matricula + ", nome: " + nome + "}");
    }

    public String getNome(String aluno) {
        String nome = aluno.substring(aluno.indexOf("nome:") + 6, aluno.indexOf("}"));
        return nome;
    }

    public String getMatricula(String aluno) {
        String matricula = aluno.substring(aluno.indexOf("matricula:") + 11, aluno.indexOf(","));
        return matricula;
    }
}
