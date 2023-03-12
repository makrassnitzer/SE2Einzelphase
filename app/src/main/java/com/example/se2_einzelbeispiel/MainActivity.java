package com.example.se2_einzelbeispiel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editTextInput = findViewById(R.id.editText_input);
        TextView textView = findViewById(R.id.textView_answer);
        Button sendButton = findViewById(R.id.button_send);
        Button calcButton = findViewById(R.id.button_calc);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TcpConnection(editTextInput.getText().toString()).execute();
            }
        });
        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String output;
                if (editTextInput.getText().length() == 8) {
                    output = filterPrimeNumbers(editTextInput.getText().toString());
                } else {
                    output = "Die Matrikelnummer muss 8 Zahlen beinhalten.";
                }
                textView.setText(output);
            }
        });
    }

    public String filterPrimeNumbers(String numbers) {
        String result = "";
        for (int i = 0; i < numbers.length(); i++) {
            int number = Integer.parseInt(numbers.substring(i, i + 1));
            if (isPrimeNumber(number)) {
                result += number;
            }
        }
        if (result.length() == 0) {
            result = "Die Matrikelnummer beinhaltet keine Primzahlen.";
        }
        return result;
    }

    public boolean isPrimeNumber(int number) {
        boolean result = true;
        if (number <= 1) {
            result = false;
        }
        for (int i = 2; i < number; i++) {
            if (number % i == 0) {
                result = false;
            }
        }
        return result;
    }

    public class TcpConnection extends AsyncTask<Void, Void, String> {

        private final String host = "se2-isys.aau.at";
        private final int port = 53212;
        private String input;

        public TcpConnection(String input) {
            this.input = input;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try {
                Socket socket = new Socket(host, port);
                PrintWriter outputStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                outputStream.write(input + "\n");
                outputStream.flush();
                result = inputStream.readLine();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String output) {
            TextView textView = findViewById(R.id.textView_answer);
            textView.setText(output);
        }
    }
}