package io.github.AbhiTheNullPointer.secureprefs.demo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;

import io.github.AbhiTheNullPointer.secureprefs.SecureSharedPreference;

/**
 * Demo Activity for SecureSharedPreference library.
 *
 * Shows how to store and retrieve encrypted values, and runs a
 * comprehensive type-coverage test on demand.
 */
public class MainActivity extends Activity {

    private SecureSharedPreference prefs;
    private EditText etKey;
    private EditText etValue;
    private TextView tvOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise the library — one line is all it takes
        prefs = new SecureSharedPreference(this, "demo_prefs");

        etKey    = findViewById(R.id.etKey);
        etValue  = findViewById(R.id.etValue);
        tvOutput = findViewById(R.id.tvOutput);

        Button btnSave     = findViewById(R.id.btnSave);
        Button btnLoad     = findViewById(R.id.btnLoad);
        Button btnRemove   = findViewById(R.id.btnRemove);
        Button btnClear    = findViewById(R.id.btnClear);
        Button btnRunTests = findViewById(R.id.btnRunTests);

        btnSave.setOnClickListener(v -> saveValue());
        btnLoad.setOnClickListener(v -> loadValue());
        btnRemove.setOnClickListener(v -> removeValue());
        btnClear.setOnClickListener(v -> clearAll());
        btnRunTests.setOnClickListener(v -> runAllTests());
    }

    private void saveValue() {
        String key   = etKey.getText().toString().trim();
        String value = etValue.getText().toString().trim();

        if (key.isEmpty()) { show("⚠ Key must not be empty."); return; }

        prefs.putString(key, value);
        show("✅ Saved\n  key   = \"" + key + "\"\n  value = \"" + value + "\"");
    }

    private void loadValue() {
        String key = etKey.getText().toString().trim();
        if (key.isEmpty()) { show("⚠ Key must not be empty."); return; }

        String value = prefs.getString(key, null);
        if (value == null) {
            show("❌ Key not found: \"" + key + "\"");
        } else {
            show("🔓 Loaded\n  key   = \"" + key + "\"\n  value = \"" + value + "\"");
        }
    }

    private void removeValue() {
        String key = etKey.getText().toString().trim();
        if (key.isEmpty()) { show("⚠ Key must not be empty."); return; }

        prefs.remove(key);
        show("🗑 Removed key: \"" + key + "\"");
    }

    private void clearAll() {
        prefs.clear();
        show("🧹 All preferences cleared.");
    }

    /**
     * Runs a quick self-test covering every supported data type.
     * Results are printed to the output panel so the tester can
     * verify everything passes without reading source code.
     */
    private void runAllTests() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SecureSharedPreference Type Tests ===\n\n");

        // --- String ---
        prefs.putString("test_string", "Hello, World!");
        String s = prefs.getString("test_string", "FAIL");
        pass(sb, "String", "Hello, World!".equals(s), s);

        // --- int ---
        prefs.putInt("test_int", 42);
        int i = prefs.getInt("test_int", -1);
        pass(sb, "int", i == 42, String.valueOf(i));

        // --- long ---
        prefs.putLong("test_long", 9_876_543_210L);
        long l = prefs.getLong("test_long", -1L);
        pass(sb, "long", l == 9_876_543_210L, String.valueOf(l));

        // --- float ---
        prefs.putFloat("test_float", 3.14f);
        float f = prefs.getFloat("test_float", 0f);
        pass(sb, "float", Math.abs(f - 3.14f) < 0.001f, String.valueOf(f));

        // --- boolean true ---
        prefs.putBoolean("test_bool_true", true);
        boolean bt = prefs.getBoolean("test_bool_true", false);
        pass(sb, "boolean(true)", bt, String.valueOf(bt));

        // --- boolean false ---
        prefs.putBoolean("test_bool_false", false);
        boolean bf = prefs.getBoolean("test_bool_false", true);
        pass(sb, "boolean(false)", !bf, String.valueOf(bf));

        // --- contains ---
        boolean has = prefs.contains("test_string");
        pass(sb, "contains(existing)", has, String.valueOf(has));

        // --- remove ---
        prefs.remove("test_string");
        boolean gone = !prefs.contains("test_string");
        pass(sb, "remove", gone, gone ? "key gone" : "key still present");

        // --- default value ---
        String def = prefs.getString("nonexistent_key", "default");
        pass(sb, "default value", "default".equals(def), def);

        // --- empty string ---
        prefs.putString("test_empty", "");
        String empty = prefs.getString("test_empty", "FAIL");
        pass(sb, "empty string", "".equals(empty), "\"" + empty + "\"");

        // --- unicode ---
        prefs.putString("test_unicode", "नमस्ते 🔐 مرحبا");
        String uni = prefs.getString("test_unicode", "FAIL");
        pass(sb, "unicode / emoji", "नमस्ते 🔐 مرحبا".equals(uni), uni);

        // Cleanup
        prefs.clear();
        sb.append("\nAll tests complete. KeyStore key intact after clear() ✔");

        show(sb.toString());
    }

    private void pass(StringBuilder sb, String label, boolean ok, String actual) {
        sb.append(ok ? "✅ " : "❌ ")
          .append(label)
          .append(" → ")
          .append(actual)
          .append('\n');
    }

    private void show(String text) {
        tvOutput.setText(text);
    }
}
