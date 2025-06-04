package com.example.aymara_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;




public class ContacFragment extends Fragment {

    private RadioGroup sexo, nutricion;
    private TextView name, mail, edad, altura, peso, objetivos, comentario;
    private View fragmentView;
    private Button enviar, borrar;

    public ContacFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_contac, container, false);

        name = fragmentView.findViewById(R.id.ed_name);
        mail = fragmentView.findViewById(R.id.em_mail);
        edad = fragmentView.findViewById(R.id.en_edad);
        sexo = fragmentView.findViewById(R.id.rbg_sexo);
        altura = fragmentView.findViewById(R.id.end_altura);
        peso = fragmentView.findViewById(R.id.end_peso);
        objetivos = fragmentView.findViewById(R.id.ed_objetivo);
        nutricion = fragmentView.findViewById(R.id.rbg_nutricion);

        enviar = fragmentView.findViewById(R.id.btn_enviar);
        borrar = fragmentView.findViewById(R.id.btn_borrar);

        comentario = fragmentView.findViewById(R.id.ed_comentario);



        enviar.setOnClickListener(v -> enviarDatos());
        borrar.setOnClickListener(v -> borrar());

        return fragmentView;
    }

    public void enviarDatos(){

        if(verificarCampos()){
            Toast.makeText(getContext(), "Datos enviados correctamente " +
                    "\nNombre: "+ name.getText().toString() +
                    "\nMail: " + mail.getText().toString() +
                    "\nEdad: " + edad.getText().toString() +
                    "\nAltura: " + altura.getText().toString() +
                    "\nPeso: " + peso.getText().toString() +
                    "\nObjetivo: " + objetivos.getText().toString() +
                    "\nSexo: " + opcionRadiobuton(sexo) +
                    "\nNutricion: " + opcionRadiobuton(nutricion) +
                    "\nComentario: " + comentario.getText().toString(), Toast.LENGTH_SHORT).show();
            borrar();
        }
        else
            Toast.makeText(getContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();

    }

    public void borrar(){
        name.setText("");
        mail.setText("");
        edad.setText("");
        altura.setText("");
        peso.setText("");
        objetivos.setText("");

        // Deseleccionar los RadioButton
        sexo.clearCheck();
        nutricion.clearCheck();
        comentario.setText("");

        // Mostrar un mensaje de confirmación
        Toast.makeText(getContext(), "Campos borrados", Toast.LENGTH_SHORT).show();

    }

    public String opcionRadiobuton(RadioGroup g){

        int seleccion = g.getCheckedRadioButtonId();
        RadioButton b = fragmentView.findViewById(seleccion);
        if (b != null)
            return b.getText().toString();
        else
            return "Opcion no seleccionada";
    }



    public boolean verificarCampos() {

        String nameText = name.getText().toString().trim();
        if (nameText.length() < 3 || nameText.length() > 35 ||
                !nameText.contains(" ") ||
                !nameText.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {

            name.setError("Formato Incorrecto. Debe incluir nombre y apellido, y solo letras.");
            return false;
        }

        // Validar correo con un '@'
        String mailText = mail.getText().toString().trim();
        if (!mailText.matches("^[\\w\\.-]+@[\\w\\.-]+\\.com$")) {
            mail.setError("Especificar ej:aymara@gmail.com");
            return false;
        }

        // Validar edad con máximo 3 dígitos
        String edadText = edad.getText().toString().trim();
        if (!edadText.matches("\\d{1,3}")) {
            edad.setError("Formato Incorrecto de edad.");
            return false;
        }

        // Validar altura con máximo 3 dígitos
        String alturaText = altura.getText().toString().trim();
        if (!alturaText.matches("\\d{2,3}")) {
            altura.setError("Formato Incorrecto de Altura.");
            return false;
        }

        // Validar peso con máximo 3 dígitos
        String pesoText = peso.getText().toString().trim();
        if (!pesoText.matches("\\d{2,3}")) {
            peso.setError("Formato Incorrecto de Peso.");
            return false;
        }

        // Validar comentario
        String comentarioText = comentario.getText().toString().trim();
        if (comentarioText.length() < 10 || comentarioText.length() > 150) {
            comentario.setError("Comentario Invalido.");
            return false;
        }




        return !name.getText().toString().trim().isEmpty() &&
                !mail.getText().toString().trim().isEmpty() &&
                !edad.getText().toString().trim().isEmpty() &&
                !altura.getText().toString().trim().isEmpty() &&
                !peso.getText().toString().trim().isEmpty() &&
                !objetivos.getText().toString().trim().isEmpty() &&
                (sexo.getCheckedRadioButtonId() != -1) &&
                (nutricion.getCheckedRadioButtonId() != -1) &&
                !comentario.getText().toString().trim().isEmpty();

    }

}