package com.example.mediauva;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    EditText nota1EditText, nota2EditText, nota3EditText;
    Button calcButton;
    TextView resultText;
    TextInputLayout nota3InputLayout;

    private boolean isRecuperacao = false;
    private double nota1Original = 0;
    private double nota2Original = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        nota1EditText = findViewById(R.id.nota1);
        nota2EditText = findViewById(R.id.nota2);
        nota3EditText = findViewById(R.id.nota3); // EditText para nota 3 (recuperação)
        calcButton = findViewById(R.id.calcButton);
        resultText = findViewById(R.id.resultText);
        nota3InputLayout = findViewById(R.id.nota3InputLayout); // Layout para nota 3 (recuperação)

        nota3InputLayout.setVisibility(View.GONE); // Inicia Layout 3 com visibilidade GONE
        resultText.setText(""); // Limpa o resultado inicial

        calcButton.setOnClickListener(v -> {
            if (!isRecuperacao) {
                calculaMedia(); // Calcula a média se não estiver em recuperação
            } else {
                calculaMediaRecuperacao(); // Calcula a média se estiver em recuperação
            }
        });

    }

    private void calculaMedia() {
        resultText.setText(""); // Limpa o resultado anterior)
        clearErrors(); // Limpa os erros anteriores
        String s1 = nota1EditText.getText().toString().trim();
        String s2 = nota2EditText.getText().toString().trim();

        if (s1.isEmpty()) {
            nota1EditText.setError("Preencha a nota da A1"); // Caso a nota 1 esteja vazia
            return;
        }
        if (s2.isEmpty()) {
            nota2EditText.setError("Preencha a nota da A2"); // Caso a nota 2 esteja vazia
            return;
        }

        try {
            double nota1 = Double.parseDouble(s1); // Converte a nota 1 para double
            double nota2 = Double.parseDouble(s2); // Converte a nota 2 para double

            if (isValidNota(nota1)) {
                nota1EditText.setError("Nota da A1 deve estar entre 0 e 10"); // Caso a nota 1 não esteja entre 0 e 10
                return;
            }

            if (isValidNota(nota2)) {
                nota2EditText.setError("Nota da A2 deve estar entre 0 e 10"); // Caso a nota 2 não esteja entre 0 e 10
                return;
            }

            nota1Original = nota1; // Salva a nota 1 original
            nota2Original = nota2; // Salva a nota 2 original
            double media = (nota1 + nota2) / 2; // Calcula a média

            if (media >= 6) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showResult("Aprovado! Média: " + String.format("%.2f", media), getColor(R.color.colorAprovado)); // Exibe o resultado se a média for maior ou igual a 6
                }
                resetParaEstadoInicial(true); // Reseta o estado para o estado inicial
            } else {
                resultText.setText("Reprovado! Média: " + String.format("%.2f", media) + ". Insira a nota da A3 para recuperação."); // Exibe o resultado se a média for menor que 6
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    resultText.setTextColor(getColor(R.color.colorReprovado)); // Define a cor do texto do resultado
                }
                fadeInView(resultText); // Anima o resultado para aparecer)

                nota3InputLayout.setVisibility(View.VISIBLE); // Torna o layout 3 visível
                fadeInView(nota3InputLayout); // Anima o layout 3 para aparecer

                nota3EditText.requestFocus(); // Foca o EditText da nota 3
                nota1EditText.setEnabled(false); // Desabilita a nota 1
                nota2EditText.setEnabled(false); // Desabilita a nota 2
                calcButton.setText("Calcular Média Final"); // Altera o texto do botão
                isRecuperacao = true; // Define o estado para recuperação
            }
        } catch (NumberFormatException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showResult("Erro: Notas devem ser números válidos", getColor(R.color.colorReprovado)); // Exibe um erro se as notas não forem números válidos
            }
            resetParaEstadoInicial(false); // Reseta o estado para o estado inicial
        }
    }

    private void calculaMediaRecuperacao() {
        resultText.setText(""); // Limpa o resultado anterior
        clearErrors(); // Limpa os erros anteriores
        String s3 = nota3EditText.getText().toString().trim(); // Obtém a nota 3

        if (s3.isEmpty()) {
            nota3EditText.setError("Preencha a nota da A3"); // Caso a nota 3 esteja vazia
            return;
        }

        try {
            double nota3 = Double.parseDouble(s3); // Converte a nota 3 para double

            if (isValidNota(nota3)) {
                nota3EditText.setError("Nota da A3 deve estar entre 0 e 10"); // Caso a nota 3 não esteja entre 0 e 10
                return;
            }

            double novaNota1 = nota1Original; // recupera a nota 1 original
            double novaNota2 = nota2Original; // recupera a nota 2 original

            if (nota1Original < nota2Original) {
                if (nota3 > nota1Original)
                    novaNota1 = nota3; // Se a nota 3 for maior que a nota 1, a nova nota 1 é a nota 3
            } else {
                if (nota3 > nota2Original)
                    novaNota2 = nota3; // Se a nota 3 for maior que a nota 2, a nova nota 2 é a nota 3
            }

            double mediaRecuperacao = (novaNota1 + novaNota2) / 2; // Calcula a média de recuperação

            String mensagemPrefix = "Media final (Rec): " + String.format("%.2f", mediaRecuperacao); // Mensagem de resultado
            String notasOriginais = "\nNota A1: " + String.format("%.2f", nota1Original) + ", Nota A2: " + String.format("%.2f", nota2Original); // Mensagem das notas originais
            String notaRecuperacao = "\nNota A3: " + String.format("%.2f", nota3); // Mensagem da nota 3"
            String notasConsideradas = "\nNotas consideradas: " + String.format("%.2f", novaNota1) + ", " + String.format("%.2f", novaNota2); // Mensagem das notas consideradas

            if (mediaRecuperacao >= 6.0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showResult("✅ Aprovado! " + mensagemPrefix + notasOriginais + notaRecuperacao + notasConsideradas, getColor(R.color.colorAprovado)); // Exibe o resultado se a média de recuperação for maior ou igual a 6
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showResult("❌ Reprovado. " + mensagemPrefix + notasOriginais + notaRecuperacao + notasConsideradas, getColor(R.color.colorReprovado)); // Exibe o resultado se a média de recuperação for menor que 6
                }
            }
            resetParaEstadoInicial(true); // Reseta o estado para o estado inicial
        } catch (NumberFormatException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showResult("Erro: Nota da A3 deve ser um número válido", getColor(R.color.colorReprovado)); // Exibe um erro se a nota 3 não for um número válido
            }

            if (nota3InputLayout.getVisibility() == View.VISIBLE) { // Se o layout 3 estiver visível
                nota3EditText.requestFocus(); // Foca o EditText da nota 3
            }
        }
    }

    private boolean isValidNota(double nota) {
        return !(nota >= 0) || !(nota <= 10); // Verifica se a nota está entre 0 e 10
    }

    private void showResult(String mensagem, int color) {
        resultText.setVisibility(View.VISIBLE); // Torna o resultado visível
        resultText.setText(mensagem); // Define o texto do resultado
        resultText.setTextColor(color); // Define a cor do texto do resultado
        fadeInView(resultText); // Anima o resultado para aparecer
    }

    private void fadeInView(View view) {
        if (view.getVisibility() == View.GONE) { // Se a view estiver invisível
            view.setVisibility(View.VISIBLE); // Torna a view visível
        }
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f); // Animação de fade in
        fadeIn.setDuration(600); // Define a duração da animação
        view.startAnimation(fadeIn); // Inicia a animação
    }

    private void resetParaEstadoInicial(boolean limparInputFields) {
        nota3InputLayout.setVisibility(View.GONE); // Torna o layout 3 invisível
        nota3EditText.setText(""); // Limpa o EditText da nota 3
        nota1EditText.setEnabled(true); // Habilita a nota 1
        nota2EditText.setEnabled(true); // Habilita a nota 2

        if (limparInputFields) {
            nota1EditText.setText(""); // Limpa o EditText da nota 1
            nota2EditText.setText(""); // Limpa o EditText da nota 2
            if (nota1EditText.getVisibility() == View.VISIBLE) { // Se o EditText da nota 1 estiver visível
                nota1EditText.requestFocus(); // Foca o EditText da nota 1
            } else {
                if (nota3InputLayout.getVisibility() == View.VISIBLE) { // Se o layout 3 estiver visível
                    nota3EditText.requestFocus(); // Foca o EditText da nota 3
                }
            }
            calcButton.setText("Calcular Média"); // Altera o texto do botão
            isRecuperacao = false; // Define o estado para não estar em recuperação
            nota1Original = 0; // Reseta a nota 1 original
            nota2Original = 0; // Reseta a nota 2 original
        }
    }

    private void clearErrors() {
        nota1EditText.setError(null); // Limpa o erro do EditText da nota 1
        nota2EditText.setError(null); // Limpa o erro do EditText da nota 2
    }
}


