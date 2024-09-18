package com.example.aymara_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;




public class ContacFragment extends Fragment {

    private CheckBox check_si, check_no;
    private TextView tv_name, tv_mail, tv_edad, tv_sexo, tv_altura, tv_peso, tv_objetivo;

    public ContacFragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContacFragment.
     */
    /*TODO: Rename and change types and number of parameters*/
    /*public static ContacFragment newInstance(String param1, String param2) {
        ContacFragment fragment = new ContacFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contac, container, false);

        tv_name = view.findViewById(R.id.tv_name);
        tv_mail = view.findViewById(R.id.tv_mail);
        tv_edad = view.findViewById(R.id.tv_edad);
        tv_sexo = view.findViewById(R.id.tv_sexo);
        tv_altura = view.findViewById(R.id.tv_altura);
        tv_peso = view.findViewById(R.id.tv_peso);
        tv_objetivo = view.findViewById(R.id.tv_objetivo);
        check_si = view.findViewById(R.id.check_si);
        check_no = view.findViewById(R.id.check_no);

        return view;
    }

    public void enviar (){

        if (verificarCampos())
            Toast.makeText(getContext(), "Campos enviados correctamente", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "Campos vacios", Toast.LENGTH_SHORT).show();
    }

    public void borrar (){

        borrado();

    }

    public boolean verificarCampos(){
        return !tv_name.getText().toString().trim().isEmpty() &&
                !tv_mail.getText().toString().trim().isEmpty() &&
                !tv_edad.getText().toString().trim().isEmpty() &&
                !tv_sexo.getText().toString().trim().isEmpty() &&
                !tv_altura.getText().toString().trim().isEmpty() &&
                !tv_peso.getText().toString().trim().isEmpty() &&
                !tv_objetivo.getText().toString().trim().isEmpty() &&
                (check_si.isChecked() || check_no.isChecked());
    }

    private void borrado() {
        tv_name.setText("");
        tv_mail.setText("");
        tv_edad.setText("");
        tv_sexo.setText("");
        tv_altura.setText("");
        tv_peso.setText("");
        tv_objetivo.setText("");
        check_si.setChecked(false);
        check_no.setChecked(false);
    }
}