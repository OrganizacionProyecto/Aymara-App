package com.example.aymara_app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class ContacFragment extends Fragment {

    private RadioGroup sexo, nutricion;
    private EditText name, mail, edad, altura, peso, objetivos, comentario;
    private LinearLayout layoutNutricion;
    private View fragmentView;
    private Button enviar, borrar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_contac, container, false);

        // Cambié TextView por EditText para campos editables
        name = fragmentView.findViewById(R.id.ed_name);
        mail = fragmentView.findViewById(R.id.em_mail);
        edad = fragmentView.findViewById(R.id.en_edad);
        sexo = fragmentView.findViewById(R.id.rbg_sexo);
        altura = fragmentView.findViewById(R.id.end_altura);
        peso = fragmentView.findViewById(R.id.end_peso);
        objetivos = fragmentView.findViewById(R.id.ed_objetivo);
        nutricion = fragmentView.findViewById(R.id.rbg_nutricion);
        comentario = fragmentView.findViewById(R.id.ed_comentario);
        enviar = fragmentView.findViewById(R.id.btn_enviar);
        borrar = fragmentView.findViewById(R.id.btn_borrar);

        // Grupo contenedor para altura y peso que mostrar/ocultar
        layoutNutricion = fragmentView.findViewById(R.id.layout_nutricion);

        // Listener para mostrar/ocultar altura y peso según selección
        nutricion.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = fragmentView.findViewById(checkedId);
            if (selected != null && selected.getText().toString().equalsIgnoreCase("Sí")) {
                layoutNutricion.setVisibility(View.VISIBLE);
            } else {
                layoutNutricion.setVisibility(View.GONE);
                altura.setText("");
                peso.setText("");
            }
        });

        enviar.setOnClickListener(v -> enviarDatos());
        borrar.setOnClickListener(v -> borrar());

        return fragmentView;
    }

    public void enviarDatos() {
        if (verificarCampos()) {
            Toast.makeText(getContext(), "Datos enviados correctamente " +
                    "\nNombre: " + name.getText().toString() +
                    "\nMail: " + mail.getText().toString() +
                    "\nEdad: " + edad.getText().toString() +
                    (layoutNutricion.getVisibility() == View.VISIBLE
                            ? ("\nAltura: " + altura.getText().toString() +
                            "\nPeso: " + peso.getText().toString())
                            : "") +
                    "\nObjetivo: " + objetivos.getText().toString() +
                    "\nSexo: " + opcionRadiobuton(sexo) +
                    "\nNutricion: " + opcionRadiobuton(nutricion) +
                    "\nComentario: " + comentario.getText().toString(), Toast.LENGTH_LONG).show();
            borrar();
        } else {
            Toast.makeText(getContext(), "Por favor, completa todos los campos correctamente.", Toast.LENGTH_SHORT).show();
        }
    }

    public void borrar() {
        name.setText("");
        mail.setText("");
        edad.setText("");
        altura.setText("");
        peso.setText("");
        objetivos.setText("");
        sexo.clearCheck();
        nutricion.clearCheck();
        comentario.setText("");
        layoutNutricion.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Campos borrados", Toast.LENGTH_SHORT).show();
    }

    public String opcionRadiobuton(RadioGroup g) {
        int seleccion = g.getCheckedRadioButtonId();
        if (seleccion == -1) return "No seleccionado";
        RadioButton b = fragmentView.findViewById(seleccion);
        return (b != null) ? b.getText().toString() : "No seleccionado";
    }

    public boolean verificarCampos() {
        String nameText = name.getText().toString().trim();
        if (nameText.length() < 3 || nameText.length() > 35 ||
                !nameText.contains(" ") ||
                !nameText.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            name.setError("Debe tener nombre y apellido (solo letras).");
            return false;
        }

        String mailText = mail.getText().toString().trim();
        if (!mailText.matches("^[\\w\\.-]+@[\\w\\.-]+\\.com$")) {
            mail.setError("Formato de correo inválido (ej: aymara@gmail.com)");
            return false;
        }

        String edadText = edad.getText().toString().trim();
        if (!edadText.matches("\\d{1,3}")) {
            edad.setError("Edad inválida");
            return false;
        }

        // Solo validar altura y peso si está visible (es decir, seleccionaron Sí en nutrición)
        if (layoutNutricion.getVisibility() == View.VISIBLE) {
            String alturaText = altura.getText().toString().trim();
            if (!alturaText.matches("\\d{3}")) {
                altura.setError("Altura debe tener exactamente 3 dígitos (ej: 175)");
                return false;
            }

            String pesoText = peso.getText().toString().trim();
            if (!pesoText.matches("\\d{2,3}")) {
                peso.setError("Peso debe tener 2 o 3 dígitos (ej: 70)");
                return false;
            }
        }

        String comentarioText = comentario.getText().toString().trim();
        if (comentarioText.length() < 10 || comentarioText.length() > 150) {
            comentario.setError("Comentario debe tener entre 10 y 150 caracteres.");
            return false;
        }

        if (objetivos.getText().toString().trim().isEmpty()) {
            objetivos.setError("Debe completar el objetivo");
            return false;
        }

        if (sexo.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Debe seleccionar sexo", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (nutricion.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Debe seleccionar asesoramiento nutricional", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
