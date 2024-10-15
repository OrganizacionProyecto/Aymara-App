package com.example.aymara_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

public class ProfileFragment extends Fragment {

    private EditText etName, etLastName, etEmail, etAddress;
    private Button btnChangePassword, btnDeleteAccount, btnLogout;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        // Inicializar los campos
        etName = view.findViewById(R.id.etName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etAddress = view.findViewById(R.id.etAddress);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        btnLogout = view.findViewById(R.id.btnOut);

        // Configurar botones
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
        btnLogout.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cambiar Contraseña");

        // Crear un LinearLayout para contener los EditText
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16); // Padding para el layout

        // Crear EditText para nueva contraseña
        EditText etNewPassword = new EditText(getActivity());
        etNewPassword.setHint("Nueva Contraseña");
        etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPassword);

        // Crear EditText para confirmar contraseña
        EditText etConfirmPassword = new EditText(getActivity());
        etConfirmPassword.setHint("Confirmar Contraseña");
        etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etConfirmPassword);

        builder.setView(layout)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String newPassword = etNewPassword.getText().toString();
                    String confirmPassword = etConfirmPassword.getText().toString();
                    if (newPassword.equals(confirmPassword) && isValidPassword(newPassword)) {
                        updatePassword(newPassword);
                    } else {
                        Toast.makeText(getActivity(), "Las contraseñas no coinciden o son inválidas", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6; // Ejemplo de validación
    }

    private void updatePassword(String newPassword) {
        // Implementa la lógica para actualizar la contraseña
        Toast.makeText(getActivity(), "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
    }


    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Eliminar Cuenta")
                .setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    deleteAccount();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void deleteAccount() {
        // Aquí puedes implementar la lógica para eliminar la cuenta
        // Por ejemplo, llamar a un método en tu ViewModel o hacer una llamada a la API
        Toast.makeText(getActivity(), "Cuenta eliminada con éxito", Toast.LENGTH_SHORT).show();
    }


    private void logoutUser() {
        // Aquí puedes implementar la lógica para cerrar sesión
        /*SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Eliminar todos los datos de sesión
        editor.apply();

        // Redirigir al usuario a la pantalla de inicio de sesión
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish(); // Terminar la actividad actual

         */
    }

}
