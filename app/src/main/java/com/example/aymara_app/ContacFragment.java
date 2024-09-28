package com.example.aymara_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;




public class ContacFragment extends Fragment {

    private RadioGroup sexo, nutricion;
    private TextView name, mail, edad, altura, peso, objetivos;
    private View fragmentView;

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

        Button enviar = fragmentView.findViewById(R.id.btn_enviar);
        Button borrar = fragmentView.findViewById(R.id.btn_borrar);

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
                    "\nNutricion: " + opcionRadiobuton(nutricion), Toast.LENGTH_SHORT).show();
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



    public boolean verificarCampos(){
        return !name.getText().toString().trim().isEmpty() &&
                !mail.getText().toString().trim().isEmpty() &&
                !edad.getText().toString().trim().isEmpty() &&
                !altura.getText().toString().trim().isEmpty() &&
                !peso.getText().toString().trim().isEmpty() &&
                !objetivos.getText().toString().trim().isEmpty() &&
                (sexo.getCheckedRadioButtonId() != -1) &&
                (nutricion.getCheckedRadioButtonId() != -1);
    }

}