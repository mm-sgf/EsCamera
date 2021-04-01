package com.cfox.escamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CaptureResult;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cfox.camera.capture.PreviewStateListener;
import com.cfox.camera.log.EsLog;
import com.cfox.camera.utils.OrientationFilter;
import com.cfox.camera.utils.OrientationSensorManager;


public class CameraMainFragment extends Fragment implements PreviewStateListener {
    private static final String TAG = "CameraMainFragment";
    private AutoFitTextureView mPreviewView;
    private SurfaceProviderImpl mSurfaceHelperImpl;
    private EsyCameraController mCameraController;
    private FocusView mFocusView;
    private OrientationFilter mOrientationFilter;
    private TextView mPermissionView;


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity() != null
                    && (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                return true;
            } else {
                requestPermissions( new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            boolean hasPermission = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false;
                    break;
                }
            }

            if (hasPermission) {
                openCamera();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCameraController = new EsyCameraController(getActivity());
        OrientationSensorManager manager =  OrientationSensorManager.getInstance();
        manager.init(getActivity());
        mOrientationFilter = new OrientationFilter(manager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPermissionView = view.findViewById(R.id.permission_view);
        mFocusView = new FocusView(getActivity());
        mFocusView.initFocusArea(400, 400);
        mFocusView.setVisibility(View.GONE);
        ((FrameLayout)view.findViewById(R.id.root_view)).addView(mFocusView);
        mPreviewView = view.findViewById(R.id.preview_view);


        view.findViewById(R.id.btn_torch_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraController.torchFlash();
            }
        });

        view.findViewById(R.id.btn_on_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraController.onFlash();
            }
        });

        view.findViewById(R.id.btn_auto_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraController.autoFlash();
            }
        });


        view.findViewById(R.id.btn_close_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraController.closeFlash();
            }
        });

        view.findViewById(R.id.btn_open_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurfaceHelperImpl = new SurfaceProviderImpl(mPreviewView);
                mCameraController.backCamera(mSurfaceHelperImpl, CameraMainFragment.this);
            }
        });
        view.findViewById(R.id.btn_open_font).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurfaceHelperImpl = new SurfaceProviderImpl(mPreviewView);
                mCameraController.fontCamera(mSurfaceHelperImpl, CameraMainFragment.this);
            }
        });

        view.findViewById(R.id.btn_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraController.photoModule();
                mSurfaceHelperImpl = new SurfaceProviderImpl(mPreviewView);
                mCameraController.backCamera(mSurfaceHelperImpl, CameraMainFragment.this);
            }
        });

        view.findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraController.videoModule();
                mSurfaceHelperImpl = new SurfaceProviderImpl(mPreviewView);
                mCameraController.backCamera(mSurfaceHelperImpl, CameraMainFragment.this);
            }
        });

        view.findViewById(R.id.btn_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraController.capture();
            }
        });

        ((SeekBar)view.findViewById(R.id.seek_ev)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showMsg("ev :" + progress);
                mCameraController.setEv(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((SeekBar)view.findViewById(R.id.seek_focus)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showMsg("zoom :" + progress);
                mCameraController.setZoom(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mPreviewView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mOrientationFilter.setOnceListener(new OrientationFilter.OrientationChangeListener() {
                        @Override
                        public void onChanged() {
                            mCameraController.resetFocus();
                        }
                    });
                    mCameraController.setFocus(event.getX(), event.getY());
                    mFocusView.moveToPosition(event.getX(), event.getY());

                }
                return true;
            }
        });

    }

    private void openCamera() {
        mOrientationFilter.onResume();
        mSurfaceHelperImpl = new SurfaceProviderImpl(mPreviewView);
        EsLog.d("onResume: .......");
        mCameraController.backCamera(mSurfaceHelperImpl, this);
    }

    private void stopCamera() {
        mOrientationFilter.onPause();
        EsLog.d("onPause: ........");
        if (mCameraController == null) return;
        mCameraController.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermission()) {
            openCamera();
        } else {
            mPermissionView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (checkPermission()) {
            stopCamera();
        }
    }

    private void showMsg(String msg) {
        Toast.makeText(getContext(), "" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFocusStateChange(int state) {
        switch (state) {
            case CaptureResult.CONTROL_AF_STATE_ACTIVE_SCAN:
                mFocusView.post(new Runnable() {
                    @Override
                    public void run() {
                        mFocusView.startFocus();
                    }
                });
                break;
            case CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED:
            case CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED:
                mFocusView.post(new Runnable() {
                    @Override
                    public void run() {
                        mFocusView.focusSuccess();
                    }
                });
                break;
            case CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED:
            case CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED:
                mFocusView.post(new Runnable() {
                    @Override
                    public void run() {
                        mFocusView.focusFailed();
                    }
                });
                break;
            case CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN:
//                listener.autoFocus();
                break;
            case CaptureResult.CONTROL_AF_STATE_INACTIVE:
                mFocusView.post(new Runnable() {
                    @Override
                    public void run() {
                        mFocusView.hideFocusView();
                    }
                });
                break;
        }
    }

}
