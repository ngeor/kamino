package net.ngeor.t3.preferences;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for EnablingDependencyTest.
 *
 * @author ngeor on 11/2/2018.
 */
class EnablingDependencyTest {

    private PreferenceFragment preferenceFragment;
    private SharedPreferences sharedPreferences;
    private Preference enablePreference;
    private Preference dependencyPreference;

    @BeforeEach
    void before() {
        preferenceFragment = mock(PreferenceFragment.class);
        PreferenceManager preferenceManager = mock(PreferenceManager.class);
        sharedPreferences = mock(SharedPreferences.class);
        enablePreference = mock(Preference.class);
        dependencyPreference = mock(Preference.class);

        when(preferenceFragment.getPreferenceManager()).thenReturn(preferenceManager);
        when(preferenceFragment.findPreference("backupFolder")).thenReturn(enablePreference);
        when(preferenceFragment.findPreference("takeBackups")).thenReturn(dependencyPreference);

        when(preferenceManager.getSharedPreferences()).thenReturn(sharedPreferences);
    }

    @Test
    void whenDependencyIsNotSetToExpectedValue_disablePreference() {
        // arrange
        when(sharedPreferences.getString("takeBackups", "")).thenReturn("false");

        // act
        new EnablingDependency(preferenceFragment)
                .enablePreference("backupFolder")
                .whenPreference("takeBackups")
                .equals("true");

        // assert
        verify(enablePreference).setEnabled(false);
    }

    @Test
    void whenDependencyIsSetToExpectedValue_enablePreference() {
        // arrange
        when(sharedPreferences.getString("takeBackups", "")).thenReturn("true");

        // act
        new EnablingDependency(preferenceFragment)
                .enablePreference("backupFolder")
                .whenPreference("takeBackups")
                .equals("true");

        // assert
        verify(enablePreference).setEnabled(true);
    }

    @Test
    void whenDependencyChangesToExpectedValue_enablePreference() {
        // arrange
        when(sharedPreferences.getString("takeBackups", "")).thenReturn("false");

        new EnablingDependency(preferenceFragment)
                .enablePreference("backupFolder")
                .whenPreference("takeBackups")
                .equals("true");

        ArgumentCaptor<Preference.OnPreferenceChangeListener> listenerCaptor = ArgumentCaptor.forClass(Preference.OnPreferenceChangeListener.class);
        verify(dependencyPreference).setOnPreferenceChangeListener(listenerCaptor.capture());
        Preference.OnPreferenceChangeListener listener = listenerCaptor.getValue();
        Mockito.clearInvocations(enablePreference);

        // act
        listener.onPreferenceChange(null, "true");

        // assert
        verify(enablePreference).setEnabled(true);
    }

    @Test
    void whenDependencyChangesToUnexpectedValue_disablePreference() {
        // arrange
        when(sharedPreferences.getString("takeBackups", "")).thenReturn("true");

        new EnablingDependency(preferenceFragment)
                .enablePreference("backupFolder")
                .whenPreference("takeBackups")
                .equals("true");

        ArgumentCaptor<Preference.OnPreferenceChangeListener> listenerCaptor = ArgumentCaptor.forClass(Preference.OnPreferenceChangeListener.class);
        verify(dependencyPreference).setOnPreferenceChangeListener(listenerCaptor.capture());
        Preference.OnPreferenceChangeListener listener = listenerCaptor.getValue();
        Mockito.clearInvocations(enablePreference);

        // act
        listener.onPreferenceChange(null, "false");

        // assert
        verify(enablePreference).setEnabled(false);
    }
}