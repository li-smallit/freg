
//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.face;

import org.opencv.core.Algorithm;
import org.opencv.core.Mat;

// C++: class BIF
//javadoc: BIF
public class BIF extends Algorithm {

    protected BIF(long addr) { super(addr); }


    //
    // C++:  int getNumBands()
    //

    //javadoc: BIF::getNumBands()
    public  int getNumBands()
    {
        
        int retVal = getNumBands_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  int getNumRotations()
    //

    //javadoc: BIF::getNumRotations()
    public  int getNumRotations()
    {
        
        int retVal = getNumRotations_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  void compute(Mat image, Mat& features)
    //

    //javadoc: BIF::compute(image, features)
    public  void compute(Mat image, Mat features)
    {
        
        compute_0(nativeObj, image.nativeObj, features.nativeObj);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  int getNumBands()
    private static native int getNumBands_0(long nativeObj);

    // C++:  int getNumRotations()
    private static native int getNumRotations_0(long nativeObj);

    // C++:  void compute(Mat image, Mat& features)
    private static native void compute_0(long nativeObj, long image_nativeObj, long features_nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
