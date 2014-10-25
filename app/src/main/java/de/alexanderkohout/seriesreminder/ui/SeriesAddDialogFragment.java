package de.alexanderkohout.seriesreminder.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.alexanderkohout.seriesreminder.R;
import de.alexanderkohout.seriesreminder.SeriesControls;

/**
 * A fragment for creating a new series entry.
 */
public class SeriesAddDialogFragment extends DialogFragment {

    /**
     * The identifier of this fragment.
     */
    public static final String TAG = "SeriesAddDialog";

    /**
     * The reference for a {@link de.alexanderkohout.seriesreminder
     * .SeriesControls} handler. Will be used to create a new series.
     */
    private SeriesControls seriesControls;

    /**
     * Create a new dialog with no title.
     */
    public SeriesAddDialogFragment() {
        setStyle(STYLE_NO_TITLE, 0);
    }

    /**
     * Create a new instance of this fragment.
     *
     * @param seriesControls The handler that will be used to create a new
     *                       series.
     * @return Returns a new instance with the set {@link de.alexanderkohout
     * .seriesreminder.SeriesControls} handler.
     */
    public static SeriesAddDialogFragment newInstance(final SeriesControls seriesControls) {
        SeriesAddDialogFragment f = new SeriesAddDialogFragment();
        f.seriesControls = seriesControls;
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate custom layout for the dialog
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layoutView = inflater.inflate(R.layout.series_add, null);

        builder .setView(layoutView)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final EditText seriesNameEditText = (EditText)
                                layoutView.findViewById(R.id.seriesNameEditText);
                        final String name = seriesNameEditText.getText().toString();

                        if (seriesControls != null && !TextUtils.isEmpty(name)) {
                            seriesControls.add(name);
                        }

                        dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });

        return builder.create();
    }
}
