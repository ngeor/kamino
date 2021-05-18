package net.ngeor.t3.preferences;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ExclusiveListDependency.
 *
 * @author ngeor on 11/2/2018.
 */
class ExclusiveListDependencyTest {

    private ListPreference destinationPreference;
    private ListPreference sourcePreference;
    private PreferenceFragment preferenceFragment;

    @BeforeEach
    void before() {
        preferenceFragment = mock(PreferenceFragment.class);
        sourcePreference = mock(ListPreference.class);
        destinationPreference = mock(ListPreference.class);

        when(preferenceFragment.findPreference("a")).thenReturn(sourcePreference);
        when(preferenceFragment.findPreference("b")).thenReturn(destinationPreference);

        when(sourcePreference.getEntryValues()).thenReturn(new CharSequence[]{"on", "off"});
        when(sourcePreference.getValue()).thenReturn("on");

        when(destinationPreference.getEntryValues()).thenReturn(new CharSequence[]{"on", "off"});
        when(destinationPreference.getValue()).thenReturn("off");
    }

    @Test
    void whenSourcePreferenceToggles_destinationPreferenceToggles() {
        // arrange
        ExclusiveListDependency dependency = new ExclusiveListDependency(preferenceFragment, id -> {
        });
        dependency.sourcePreference("a")
                .destinationPreference("b")
                .toggle();

        ArgumentCaptor<Preference.OnPreferenceChangeListener> listenerCaptor = ArgumentCaptor.forClass(Preference.OnPreferenceChangeListener.class);
        verify(sourcePreference).setOnPreferenceChangeListener(listenerCaptor.capture());
        Preference.OnPreferenceChangeListener listener = listenerCaptor.getValue();

        // act
        listener.onPreferenceChange(null, "off");

        // assert
        verify(destinationPreference).setValue("on");
    }

    @Test
    void whenSourcePreferenceStaysTheSame_destinationPreferenceDoesNotToggle() {
        // arrange
        ExclusiveListDependency dependency = new ExclusiveListDependency(preferenceFragment, id -> {
        });
        dependency.sourcePreference("a")
                .destinationPreference("b")
                .toggle();

        ArgumentCaptor<Preference.OnPreferenceChangeListener> listenerCaptor = ArgumentCaptor.forClass(Preference.OnPreferenceChangeListener.class);
        verify(sourcePreference).setOnPreferenceChangeListener(listenerCaptor.capture());
        Preference.OnPreferenceChangeListener listener = listenerCaptor.getValue();

        // act
        listener.onPreferenceChange(null, "on");

        // assert
        verify(destinationPreference, never()).setValue(anyString());
    }
}