package com.kylepeplow.gamestracker.codereader;

import android.widget.ArrayAdapter;

import com.google.android.gms.vision.barcode.Barcode;

enum CodeType
{
    CODE_1D(Barcode.UPC_A | Barcode.UPC_E | Barcode.EAN_13, "1D Barcode"),
    CODE_QR(Barcode.QR_CODE, "QR Code");

    private final String mName;
    private final int mValue;
    private CodeType(int barcodeID, String name)
    {
        mValue = barcodeID;
        mName = name;
    }

    public int Value()
    {
        return mValue;
    }

    public String toString()
    {
        return mName;
    }
}

public interface CodeReaderProcessor
{
    CodeType getCodeType();
    void processFoundData(Barcode barcode);
    ArrayAdapter getArrayAdapter();
    boolean validate(Barcode a, Barcode b);
}
