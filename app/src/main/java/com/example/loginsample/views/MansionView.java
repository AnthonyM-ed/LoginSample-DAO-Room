package com.example.loginsample.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.loginsample.Door;
import com.example.loginsample.R;
import com.example.loginsample.Room;
import com.example.loginsample.RoomInfo;
import com.example.loginsample.fragments.DetailRoomFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import android.util.Log;
import android.view.MotionEvent;

import androidx.fragment.app.FragmentActivity;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MansionView extends View {

    private Paint paintRoomOutline;
    private Paint paintDoor;
    private Paint paintText;
    private static final String TAG = "MansionView";

    private List<Room> rooms = new ArrayList<>();
    private List<Door> doors = new ArrayList<>();
    private Map<Integer, RoomInfo> roomDataMap = new HashMap<>();

    public MansionView(Context context) {
        super(context);
        init();
        loadCoordinates(context);
        loadRoomData(context);
    }

    public MansionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        loadCoordinates(context);
        loadRoomData(context);
    }

    public MansionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        loadCoordinates(context);
        loadRoomData(context);
    }

    private void init() {
        paintRoomOutline = new Paint();
        paintRoomOutline.setColor(Color.DKGRAY);
        paintRoomOutline.setStyle(Paint.Style.STROKE);
        paintRoomOutline.setStrokeWidth(5);

        paintDoor = new Paint();
        paintDoor.setColor(Color.YELLOW);
        paintDoor.setStrokeWidth(10);

        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(30);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();
            final int OFFSET_Y = 300;

            for (Room room : rooms) {
                if (isTouchInsideRoom(touchX, touchY, room, OFFSET_Y)) {
                    handleRoomTouch(room);
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isTouchInsideRoom(float touchX, float touchY, Room room, int offsetY) {
        float x1 = room.getX1();
        float y1 = room.getY1() + offsetY;
        float x2 = room.getX2();
        float y2 = room.getY2() + offsetY;
        return touchX >= x1 && touchX <= x2 && touchY >= y1 && touchY <= y2;
    }

    private void handleRoomTouch(Room room) {
        String name = room.getName();
        String roomNumberStr = name.replaceAll("[^0-9]", "");

        if (!roomNumberStr.isEmpty()) {
            try {
                int roomNumber = Integer.parseInt(roomNumberStr);
                RoomInfo roomInfo = roomDataMap.get(roomNumber);
                if (roomInfo != null) {
                    showRoomDetails(roomInfo);
                } else {
                    Log.d(TAG, "Información no encontrada para Cuarto " + roomNumber);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error al convertir el número de cuarto", e);
            }
        } else {
            Log.d(TAG, "Número de cuarto no encontrado en el nombre: " + name);
        }
    }

    private void showRoomDetails(RoomInfo roomInfo) {
        DetailRoomFragment detailRoomFragment = DetailRoomFragment.newInstance(
                roomInfo.getTitle(), roomInfo.getImageUrl(), roomInfo.getDescription()
        );
        FragmentActivity activity = (FragmentActivity) getContext();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, detailRoomFragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int OFFSET_Y = 300; // Cambia este valor según lo que necesites

        // Dibujar los contornos de los cuartos
        for (Room room : rooms) {
            canvas.drawRect(room.getX1(), room.getY1() + OFFSET_Y, room.getX2(), room.getY2() + OFFSET_Y, paintRoomOutline);
            // Posicionar el nombre del cuarto en el centro, ajustado hacia abajo
            canvas.drawText(room.getName(), (room.getX1() + room.getX2()) / 2 - 40, (room.getY1() + room.getY2()) / 2 + OFFSET_Y, paintText);
        }

        // Dibujar las puertas como líneas amarillas
        for (Door door : doors) {
            canvas.drawLine(door.getX1(), door.getY1() + OFFSET_Y, door.getX2(), door.getY2() + OFFSET_Y, paintDoor);
        }
    }

    public void loadRoomData(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("cuartos.txt"); // Asegúrate de que el archivo esté en la carpeta assets
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int roomNumber = -1;
            String title = null;
            String description = null;
            String imageUrl = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Cuarto:")) {
                    roomNumber = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("Título:")) {
                    title = line.split(":")[1].trim();
                } else if (line.startsWith("Descripción:")) {
                    description = line.substring(line.indexOf(":") + 1).trim();
                } else if (line.startsWith("URL de la imagen:")) {
                    imageUrl = line.split(":")[1].trim();

                    // Convertir el nombre de la imagen en un recurso drawable
                    int imageResId = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());

                    // Guardar la información si todos los datos están completos
                    if (roomNumber != -1 && title != null && description != null && imageResId != 0) {
                        roomDataMap.put(roomNumber, new RoomInfo(title, description, imageResId));
                        roomNumber = -1;
                        title = null;
                        description = null;
                        imageUrl = null;
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCoordinates(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("coordenadas.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals("Cuarto")) {
                    String name = parts[1];
                    float x1 = Float.parseFloat(parts[2]);
                    float y1 = Float.parseFloat(parts[3]);
                    float x2 = Float.parseFloat(parts[4]);
                    float y2 = Float.parseFloat(parts[5]);
                    rooms.add(new Room(name, x1, y1, x2, y2));
                } else if (parts[0].equals("Puerta")) {
                    float x1 = Float.parseFloat(parts[1]);
                    float y1 = Float.parseFloat(parts[2]);
                    float x2 = Float.parseFloat(parts[3]);
                    float y2 = Float.parseFloat(parts[4]);
                    doors.add(new Door(x1, y1, x2, y2));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}