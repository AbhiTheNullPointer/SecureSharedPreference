package io.github.AbhiTheNullPointer.securesharedpreference.demo;

import android.app.Activity;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

import io.github.AbhiTheNullPointer.securesharedpreference.SecureSharedPreference;
/** Interactive demo for storing and retrieving encrypted preference values. */
public class MainActivity extends Activity {

    private SecureSharedPreference preferences;
    private EditText keyInput;
    private EditText valueInput;
    private TextView output;
    private Button[] actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.root);
        int initialLeft = root.getPaddingLeft();
        int initialTop = root.getPaddingTop();
        int initialRight = root.getPaddingRight();
        int initialBottom = root.getPaddingBottom();
        installWindowInsetsHandling(root, initialLeft, initialTop, initialRight, initialBottom);

        keyInput = findViewById(R.id.etKey);
        valueInput = findViewById(R.id.etValue);
        output = findViewById(R.id.tvOutput);
        actions = new Button[] {
                findViewById(R.id.btnSave), findViewById(R.id.btnLoad),
                findViewById(R.id.btnContains), findViewById(R.id.btnRemove),
                findViewById(R.id.btnClear), findViewById(R.id.btnRunTests)
        };

        actions[0].setOnClickListener(view -> saveValue());
        actions[1].setOnClickListener(view -> loadValue());
        actions[2].setOnClickListener(view -> checkExists());
        actions[3].setOnClickListener(view -> removeValue());
        actions[4].setOnClickListener(view -> clearAll());
        actions[5].setOnClickListener(view -> runTypeChecks());

        try {
            preferences = new SecureSharedPreference(getApplicationContext(), "demo_prefs");
            show("Ready", "Enter a key and value to try encrypted storage.");
        } catch (RuntimeException exception) {
            // Keep the demo open and show why secure storage could not initialize.
            setActionsEnabled(false);
            showError("Secure storage could not start", exception);
        }
    }

    private void saveValue() {
        String key = keyInput.getText().toString().trim();
        if (key.isEmpty()) {
            show("Key required", "Enter a key before saving.");
            return;
        }
        runSafely(() -> {
            String value = valueInput.getText().toString();
            preferences.setData(key, value);
            show("Saved", "Encrypted value stored for key \"" + key + "\".");
        });
    }

    private void loadValue() {
        String key = keyInput.getText().toString().trim();
        if (key.isEmpty()) {
            show("Key required", "Enter a key before loading.");
            return;
        }
        runSafely(() -> {
            if (!preferences.contains(key)) {
                show("Not found", "No value is stored for key \"" + key + "\".");
                return;
            }
            String value = preferences.getData(key, "");
            valueInput.setText(value);
            show("Loaded", "Decrypted value for key \"" + key + "\" is shown above.");
        });
    }

    private void removeValue() {
        String key = keyInput.getText().toString().trim();
        if (key.isEmpty()) {
            show("Key required", "Enter a key before removing it.");
            return;
        }
        runSafely(() -> {
            preferences.removeData(key);
            show("Removed", "Stored value for key \"" + key + "\" was removed.");
        });
    }

    private void checkExists() {
        String key = keyInput.getText().toString().trim();
        if (key.isEmpty()) {
            show("Key required", "Enter a key before checking storage.");
            return;
        }
        runSafely(() -> {
            boolean exists = preferences.contains(key);
            show("Contains check", exists ? "A value exists for key \"" + key + "\"." : "No value exists for key \"" + key + "\".");
        });
    }

    private void clearAll() {
        runSafely(() -> {
            preferences.clear();
            show("Storage cleared", "All demo values were removed. The encryption key is preserved.");
        });
    }

    private void runTypeChecks() {
        runSafely(() -> {
            StringBuilder results = new StringBuilder("SecureSharedPreference checks\n\n");
            // A unique prefix prevents the checks from overwriting values entered by the user.
            String prefix = "secureprefs_test_" + UUID.randomUUID() + "_";
            String stringKey = prefix + "string";
            String intKey = prefix + "int";
            String longKey = prefix + "long";
            String floatKey = prefix + "float";
            String trueKey = prefix + "true";
            String falseKey = prefix + "false";
            String removeKey = prefix + "remove";
            String legacyRemoveKey = prefix + "legacy_remove";
            String legacyStringKey = prefix + "legacy_string";
            String legacyIntKey = prefix + "legacy_int";
            String legacyLongKey = prefix + "legacy_long";
            String legacyFloatKey = prefix + "legacy_float";
            String legacyBooleanKey = prefix + "legacy_boolean";
            String[] testKeys = {stringKey, intKey, longKey, floatKey, trueKey, falseKey, removeKey, legacyRemoveKey, legacyStringKey, legacyIntKey, legacyLongKey, legacyFloatKey, legacyBooleanKey};
            int passed = 0;
            int total = 0;
            try {
                preferences.setData(stringKey, "Secure storage");
                String stringValue = preferences.getData(stringKey, "");
                passed += appendResult(results, "setData/getData string", "Secure storage".equals(stringValue), stringValue);
                total++;
                passed += appendResult(results, "contains existing key", preferences.contains(stringKey), String.valueOf(preferences.contains(stringKey)));
                total++;

                preferences.setData(intKey, 42);
                int intValue = preferences.getData(intKey, -1);
                passed += appendResult(results, "setData/getData int", intValue == 42, String.valueOf(intValue));
                total++;

                preferences.setData(longKey, 9876543210L);
                long longValue = preferences.getData(longKey, -1L);
                passed += appendResult(results, "setData/getData long", longValue == 9876543210L, String.valueOf(longValue));
                total++;

                preferences.setData(floatKey, 3.14f);
                float floatValue = preferences.getData(floatKey, 0f);
                passed += appendResult(results, "setData/getData float", Math.abs(floatValue - 3.14f) < 0.001f, String.valueOf(floatValue));
                total++;

                preferences.setData(trueKey, true);
                boolean trueValue = preferences.getData(trueKey, false);
                passed += appendResult(results, "setData/getData boolean true", trueValue, String.valueOf(trueValue));
                total++;

                preferences.setData(falseKey, false);
                boolean falseValue = preferences.getData(falseKey, true);
                passed += appendResult(results, "setData/getData boolean false", !falseValue, String.valueOf(falseValue));
                total++;

                preferences.setData(removeKey, "remove me");
                preferences.removeData(removeKey);
                boolean removed = !preferences.contains(removeKey);
                passed += appendResult(results, "removeData", removed, removed ? "key removed" : "key still exists");
                total++;
                passed += appendResult(results, "contains missing key", !preferences.contains(removeKey), String.valueOf(preferences.contains(removeKey)));
                total++;

                preferences.putString(legacyRemoveKey, "remove me");
                preferences.remove(legacyRemoveKey);
                boolean legacyRemoved = !preferences.contains(legacyRemoveKey);
                passed += appendResult(results, "remove", legacyRemoved, legacyRemoved ? "key removed" : "key still exists");
                total++;

                passed += appendResult(results, "string default", "fallback".equals(preferences.getData(prefix + "missing_string", "fallback")), preferences.getData(prefix + "missing_string", "fallback"));
                total++;
                passed += appendResult(results, "int default", preferences.getData(prefix + "missing_int", -7) == -7, String.valueOf(preferences.getData(prefix + "missing_int", -7)));
                total++;
                passed += appendResult(results, "long default", preferences.getData(prefix + "missing_long", -7L) == -7L, String.valueOf(preferences.getData(prefix + "missing_long", -7L)));
                total++;
                passed += appendResult(results, "float default", preferences.getData(prefix + "missing_float", -7f) == -7f, String.valueOf(preferences.getData(prefix + "missing_float", -7f)));
                total++;
                passed += appendResult(results, "boolean default", preferences.getData(prefix + "missing_boolean", true), String.valueOf(preferences.getData(prefix + "missing_boolean", true)));
                total++;

                preferences.putString(legacyStringKey, "Original API");
                String legacyString = preferences.getString(legacyStringKey, "");
                passed += appendResult(results, "putString/getString", "Original API".equals(legacyString), legacyString);
                total++;
                preferences.putInt(legacyIntKey, 24);
                int legacyInt = preferences.getInt(legacyIntKey, -1);
                passed += appendResult(results, "putInt/getInt", legacyInt == 24, String.valueOf(legacyInt));
                total++;
                preferences.putLong(legacyLongKey, 123456789L);
                long legacyLong = preferences.getLong(legacyLongKey, -1L);
                passed += appendResult(results, "putLong/getLong", legacyLong == 123456789L, String.valueOf(legacyLong));
                total++;
                preferences.putFloat(legacyFloatKey, 2.5f);
                float legacyFloat = preferences.getFloat(legacyFloatKey, 0f);
                passed += appendResult(results, "putFloat/getFloat", Math.abs(legacyFloat - 2.5f) < 0.001f, String.valueOf(legacyFloat));
                total++;
                preferences.putBoolean(legacyBooleanKey, true);
                boolean legacyBoolean = preferences.getBoolean(legacyBooleanKey, false);
                passed += appendResult(results, "putBoolean/getBoolean", legacyBoolean, String.valueOf(legacyBoolean));
                total++;

                // Verify clear() in a separate file so the demo's saved values remain untouched.
                SecureSharedPreference clearPreferences = new SecureSharedPreference(getApplicationContext(), prefix + "clear_file");
                clearPreferences.setData("clear_test", "before clear");
                clearPreferences.clear();
                boolean clearPassed = !clearPreferences.contains("clear_test");
                passed += appendResult(results, "clear removes values", clearPassed, clearPassed ? "value removed" : "value still exists");
                total++;
                clearPreferences.setData("clear_test", "after clear");
                boolean keyPreserved = "after clear".equals(clearPreferences.getData("clear_test", ""));
                passed += appendResult(results, "key remains usable after clear", keyPreserved, String.valueOf(keyPreserved));
                total++;
                clearPreferences.clear();
            } finally {
                // Remove every temporary entry even if a check fails partway through.
                for (String testKey : testKeys) preferences.removeData(testKey);
            }
            results.append("\nSummary: ").append(passed).append("/").append(total).append(" checks passed.");
            show("Checks complete", results.toString());
        });
    }

    private int appendResult(StringBuilder results, String label, boolean passed, String actual) {
        results.append(passed ? "PASS  " : "FAIL  ").append(label).append(" | value: ").append(actual).append('\n');
        return passed ? 1 : 0;
    }

    private void runSafely(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            showError("Operation failed", exception);
        }
    }

    private void showError(String title, RuntimeException exception) {
        // Storage errors are wrapped; display the underlying cause when one is available.
        Throwable cause = exception;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        String detail = cause.getMessage();
        String message = detail == null || detail.trim().isEmpty() ? cause.getClass().getSimpleName() : cause.getClass().getSimpleName() + ": " + detail;
        show(title, message);
    }

    private void show(String title, String message) {
        output.setText(title + "\n\n" + message);
    }

    private void setActionsEnabled(boolean enabled) {
        for (View action : actions) {
            action.setEnabled(enabled);
        }
    }

    @SuppressWarnings("deprecation")
    private void installWindowInsetsHandling(View root, int left, int top, int right, int bottom) {
        // Use the current insets API where available and preserve support for API 23 to 29.
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Insets systemBars = insets.getInsets(WindowInsets.Type.systemBars());
                view.setPadding(left + systemBars.left, top + systemBars.top, right + systemBars.right, bottom + systemBars.bottom);
            } else {
                view.setPadding(left + insets.getSystemWindowInsetLeft(), top + insets.getSystemWindowInsetTop(), right + insets.getSystemWindowInsetRight(), bottom + insets.getSystemWindowInsetBottom());
            }
            return insets;
        });
    }
}
