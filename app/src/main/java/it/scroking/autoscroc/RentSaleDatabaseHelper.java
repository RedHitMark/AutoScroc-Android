package it.scroking.autoscroc;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.scroking.autoscroc.models.Offer;


public class RentSaleDatabaseHelper extends SQLiteOpenHelper {

    private final static String DATABES_NAME = "AutoScrocc";
    private final static int DATABASE_VERSION = 1;
    private static final String TABLE_RENT = "Rent";
    private static final String TABLE_SALE = "Sale";

    private static int bestBuyRentPosition = -1;
    private static int bestBuySalePosition = -1;

    private Offer rentCar;
    private Offer saleCar;

    public RentSaleDatabaseHelper(Context context) {
        super(context, DATABES_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createRentTable = "CREATE TABLE " + TABLE_RENT + " (\n" +
                "licensePlate char(7) PRIMARY KEY,\n" +
                "matriculationYear  int(10) NOT NULL,\n" +
                "price decimal(10,2) NOT NULL,\n" +
                "km int(10) NOT NULL,\n" +
                "name varchar(150) NOT NULL,\n" +
                "carType varchar(50),\n" +
                "engineType varchar(50),\n" +
                "doors int(11),\n" +
                "trasmission varchar(50),\n" +
                "hp int(10),\n" +
                "kw int(10),\n" +
                "torque int(10),\n" +
                "cc int(10),\n" +
                "numCylinders int(10),\n" +
                "cylindersType varchar(50),\n" +
                "topSpeed decimal(10,5),\n" +
                "acc decimal(10,5),\n" +
                "weight decimal(10,5),\n" +
                "img varchar(65120));";

        String createSaleTable = "CREATE TABLE " + TABLE_SALE + " (\n" +
                "licensePlate char(7) PRIMARY KEY,\n" +
                "matriculationYear  int(10) NOT NULL,\n" +
                "price decimal(10,2) NOT NULL,\n" +
                "km int(10) NOT NULL,\n" +
                "name varchar(150) NOT NULL,\n" +
                "carType varchar(50),\n" +
                "engineType varchar(50),\n" +
                "doors int(11),\n" +
                "trasmission varchar(50),\n" +
                "hp int(10),\n" +
                "kw int(10),\n" +
                "torque int(10),\n" +
                "cc int(10),\n" +
                "numCylinders int(10),\n" +
                "cylindersType varchar(50),\n" +
                "topSpeed decimal(10,5),\n" +
                "acc decimal(10,5),\n" +
                "weight decimal(10,5),\n" +
                "img varchar(65120));";


        db.execSQL(createRentTable);
        db.execSQL(createSaleTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_SALE);
        db.execSQL("DROP TABLE " + TABLE_RENT);
    }

    //RENT CAR
    public List<Offer> getAllRents() {
        SQLiteDatabase db = this.getWritableDatabase();

        List<Offer> rentCarList = new ArrayList<>();
        String allElementCarQuery = "SELECT * FROM " + TABLE_RENT;
        Cursor cursor = db.rawQuery(allElementCarQuery, null);

        if (cursor.moveToFirst()) {
            do {

                rentCar = new Offer();
                rentCar.setLicensePlate(cursor.getString(0));
                rentCar.setMatriculationYear(Integer.parseInt(cursor.getString(1)));
                rentCar.setPrice(Double.parseDouble(cursor.getString(2)));
                rentCar.setKm(Integer.parseInt(cursor.getString(3)));
                rentCar.setName(cursor.getString(4));
                rentCar.setCarType(cursor.getString(5));
                rentCar.setEngineType(cursor.getString(6));
                rentCar.setDoors(Integer.parseInt(cursor.getString(7)));
                rentCar.setTrasmission(cursor.getString(8));
                rentCar.setHp(Integer.parseInt(cursor.getString(9)));
                // rentCar.setKw(Integer.parseInt(cursor.getString(10)));
                rentCar.setTorque(Integer.parseInt(cursor.getString(11)));
                rentCar.setCc(Integer.parseInt(cursor.getString(12)));
                rentCar.setNumCylinders(Integer.parseInt(cursor.getString(13)));
                rentCar.setCylindersType(cursor.getString(14));
                rentCar.setTopSpeed(Integer.parseInt(cursor.getString(15)));
                rentCar.setAcc(Double.parseDouble(cursor.getString(16)));
                rentCar.setWeight(Integer.parseInt(cursor.getString(17)));
                rentCar.setImg(cursor.getString(18));

                rentCarList.add(rentCar);

            } while (cursor.moveToNext());

            cursor.close();
        }


        return rentCarList;
    }

    public List<Offer> getRent(int km, int matriculationYear, double price, int hp, String engineType, int doors, String carType, String cylindersType, int numCylinders) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<String> queryParts = new ArrayList<>();
        List<Offer> rentCarList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_RENT;


        if (km > 0) {
            queryParts.add("km > " + km);
        }
        if (matriculationYear > 0) {
            queryParts.add(" matriculationYear > " + matriculationYear);
        }
        if (price > 0) {
            queryParts.add(" price > " + price);
        }
        if (hp > 0) {
            queryParts.add(" hp > " + hp);
        }
        if (!engineType.equals("")) {
            queryParts.add(" engineType = " + "'" + engineType + "'");
        }
        if (doors > 1) {
            queryParts.add("doors = " + doors);
        }
        if (!carType.equals("")) {
            queryParts.add("carType = " + "'" + carType + "'");
        }
        if (!cylindersType.equals("")) {
            queryParts.add("cylindersType = " + "'" + cylindersType + "'");
        }
        if (numCylinders > 0) {
            queryParts.add("numCylinders >= " + numCylinders);
        }

        if (queryParts.size() > 0) {
            query += " WHERE ";

            int i;

            StringBuilder queryBuilder = new StringBuilder(query);
            for (i = 0; i < queryParts.size() - 1; i++) {
                queryBuilder.append(queryParts.get(i)).append(" AND ");
            }
            query = queryBuilder.toString();


            query += queryParts.get(i);
        }


        Log.d("DB", query);

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                rentCar = new Offer();
                rentCar.setLicensePlate(cursor.getString(0));
                rentCar.setMatriculationYear(Integer.parseInt(cursor.getString(1)));
                rentCar.setPrice(Double.parseDouble(cursor.getString(2)));
                rentCar.setKm(Integer.parseInt(cursor.getString(3)));
                rentCar.setName(cursor.getString(4));
                rentCar.setCarType(cursor.getString(5));
                rentCar.setEngineType(cursor.getString(6));
                rentCar.setDoors(Integer.parseInt(cursor.getString(7)));
                rentCar.setTrasmission(cursor.getString(8));
                rentCar.setHp(Integer.parseInt(cursor.getString(9)));
                // rentCar.setKw(Integer.parseInt(cursor.getString(10)));
                rentCar.setTorque(Integer.parseInt(cursor.getString(11)));
                rentCar.setCc(Integer.parseInt(cursor.getString(12)));
                rentCar.setNumCylinders(Integer.parseInt(cursor.getString(13)));
                rentCar.setCylindersType(cursor.getString(14));
                rentCar.setTopSpeed(Integer.parseInt(cursor.getString(15)));
                rentCar.setAcc(Double.parseDouble(cursor.getString(16)));
                rentCar.setWeight(Integer.parseInt(cursor.getString(17)));
                rentCar.setImg(cursor.getString(18));

                rentCarList.add(rentCar);

            } while (cursor.moveToNext());

            cursor.close();
        }


        return rentCarList;
    }

    public Offer getBestRent() {
        if (bestBuyRentPosition == -1 && this.getAllSales().size() > 0) {
            bestBuyRentPosition = (new Random().nextInt(this.getAllSales().size()));
        }

        SQLiteDatabase db = this.getWritableDatabase();

        Offer rentCar = new Offer();
        //String allElementCarQuery = "SELECT * FROM " + TABLE_RENT + " ORDER BY random() LIMIT 1";
        String allElementCarQuery = "SELECT * FROM " + TABLE_RENT + " LIMIT 1 OFFSET " + bestBuyRentPosition;
        Cursor cursor = db.rawQuery(allElementCarQuery, null);

        if (cursor.moveToFirst()) {


                rentCar = new Offer();
                rentCar.setLicensePlate(cursor.getString(0));
                rentCar.setMatriculationYear(Integer.parseInt(cursor.getString(1)));
                rentCar.setPrice(Double.parseDouble(cursor.getString(2)));
                rentCar.setKm(Integer.parseInt(cursor.getString(3)));
                rentCar.setName(cursor.getString(4));
                rentCar.setCarType(cursor.getString(5));
                rentCar.setEngineType(cursor.getString(6));
                rentCar.setDoors(Integer.parseInt(cursor.getString(7)));
                rentCar.setTrasmission(cursor.getString(8));
                rentCar.setHp(Integer.parseInt(cursor.getString(9)));
                // rentCar.setKw(Integer.parseInt(cursor.getString(10)));
                rentCar.setTorque(Integer.parseInt(cursor.getString(11)));
                rentCar.setCc(Integer.parseInt(cursor.getString(12)));
                rentCar.setNumCylinders(Integer.parseInt(cursor.getString(13)));
                rentCar.setCylindersType(cursor.getString(14));
                rentCar.setTopSpeed(Integer.parseInt(cursor.getString(15)));
                rentCar.setAcc(Double.parseDouble(cursor.getString(16)));
                rentCar.setWeight(Integer.parseInt(cursor.getString(17)));
                rentCar.setImg(cursor.getString(18));




            cursor.close();
        }


        return rentCar;
    }

    public void addRent(Offer rentCar) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues rentValue = new ContentValues();

        rentValue.put("licensePlate", rentCar.getLicensePlate());
        rentValue.put("matriculationYear", rentCar.getMatriculationYear());
        rentValue.put("price", rentCar.getPrice());
        rentValue.put("km", rentCar.getKm());
        rentValue.put("name", rentCar.getName());
        rentValue.put("carType", rentCar.getCarType());
        rentValue.put("engineType", rentCar.getEngineType());
        rentValue.put("doors", rentCar.getDoors());
        rentValue.put("trasmission", rentCar.getTrasmission());
        rentValue.put("hp", rentCar.getHp());
        rentValue.put("torque", rentCar.getTorque());
        rentValue.put("cc", rentCar.getCc());
        rentValue.put("numCylinders", rentCar.getNumCylinders());
        rentValue.put("cylindersType", rentCar.getCylindersType());
        rentValue.put("topSpeed", rentCar.getTopSpeed());
        rentValue.put("acc", rentCar.getAcc());
        rentValue.put("weight", rentCar.getWeight());
        rentValue.put("img", rentCar.getImg());

        db.insert(TABLE_RENT, null, rentValue);

    }

    public void deleteTableRent() {
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteTable = "DELETE FROM " + TABLE_RENT;
        db.execSQL(deleteTable);

    }

    //SALE CAR

    public List<Offer> getAllSales() {
        SQLiteDatabase db = this.getWritableDatabase();

        List<Offer> rentCarList = new ArrayList<>();
        String allElementCarQuery = "SELECT * FROM " + TABLE_SALE;
        Cursor cursor = db.rawQuery(allElementCarQuery, null);

        if (cursor.moveToFirst()) {
            do {

                saleCar = new Offer();
                saleCar.setLicensePlate(cursor.getString(0));
                saleCar.setMatriculationYear(Integer.parseInt(cursor.getString(1)));
                saleCar.setPrice(Double.parseDouble(cursor.getString(2)));
                saleCar.setKm(Integer.parseInt(cursor.getString(3)));
                saleCar.setName(cursor.getString(4));
                saleCar.setCarType(cursor.getString(5));
                saleCar.setEngineType(cursor.getString(6));
                saleCar.setDoors(Integer.parseInt(cursor.getString(7)));
                saleCar.setTrasmission(cursor.getString(8));
                saleCar.setHp(Integer.parseInt(cursor.getString(9)));
                // rentCar.setKw(Integer.parseInt(cursor.getString(10)));
                saleCar.setTorque(Integer.parseInt(cursor.getString(11)));
                saleCar.setCc(Integer.parseInt(cursor.getString(12)));
                saleCar.setNumCylinders(Integer.parseInt(cursor.getString(13)));
                saleCar.setCylindersType(cursor.getString(14));
                saleCar.setTopSpeed(Integer.parseInt(cursor.getString(15)));
                saleCar.setAcc(Double.parseDouble(cursor.getString(16)));
                saleCar.setWeight(Integer.parseInt(cursor.getString(17)));
                saleCar.setImg(cursor.getString(18));

                rentCarList.add(saleCar);

            } while (cursor.moveToNext());

            cursor.close();
        }


        return rentCarList;
    }

    public List<Offer> getSale(int km, int matriculationYear, double price, int hp, String engineType, int doors, String carType, String cylindersType, int numCylinders) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<String> queryParts = new ArrayList<>();
        List<Offer> rentCarList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_SALE;


        if (km > 0) {
            queryParts.add("km > " + km);
        }
        if (matriculationYear > 0) {
            queryParts.add(" matriculationYear > " + matriculationYear);
        }
        if (price > 0) {
            queryParts.add(" price >" + price);
        }
        if (hp > 0) {
            queryParts.add(" hp >" + hp);
        }
        if (!engineType.equals("")) {
            queryParts.add(" engineType = " + "'" + engineType + "'");
        }
        if (doors > 1) {
            queryParts.add("doors = " + doors);
        }
        if (!carType.equals("")) {
            queryParts.add("carType = " + "'" + carType + "'");
        }
        if (!cylindersType.equals("")) {
            queryParts.add("cylindersType = " + "'" + cylindersType + "'");
        }
        if (numCylinders > 0) {
            queryParts.add("numCylinders >= " + numCylinders);
        }

        if (queryParts.size() > 0) {
            query += " WHERE ";

            int i;

            StringBuilder queryBuilder = new StringBuilder(query);
            for (i = 0; i < queryParts.size() - 1; i++) {
                queryBuilder.append(queryParts.get(i)).append(" AND ");
            }
            query = queryBuilder.toString();


            query += queryParts.get(i);
        }


        Log.d("DB", query);

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                saleCar = new Offer();
                saleCar.setLicensePlate(cursor.getString(0));
                saleCar.setMatriculationYear(Integer.parseInt(cursor.getString(1)));
                saleCar.setPrice(Double.parseDouble(cursor.getString(2)));
                saleCar.setKm(Integer.parseInt(cursor.getString(3)));
                saleCar.setName(cursor.getString(4));
                saleCar.setCarType(cursor.getString(5));
                saleCar.setEngineType(cursor.getString(6));
                saleCar.setDoors(Integer.parseInt(cursor.getString(7)));
                saleCar.setTrasmission(cursor.getString(8));
                saleCar.setHp(Integer.parseInt(cursor.getString(9)));
                // rentCar.setKw(Integer.parseInt(cursor.getString(10)));
                saleCar.setTorque(Integer.parseInt(cursor.getString(11)));
                saleCar.setCc(Integer.parseInt(cursor.getString(12)));
                saleCar.setNumCylinders(Integer.parseInt(cursor.getString(13)));
                saleCar.setCylindersType(cursor.getString(14));
                saleCar.setTopSpeed(Integer.parseInt(cursor.getString(15)));
                saleCar.setAcc(Double.parseDouble(cursor.getString(16)));
                saleCar.setWeight(Integer.parseInt(cursor.getString(17)));
                saleCar.setImg(cursor.getString(18));

                rentCarList.add(saleCar);


            } while (cursor.moveToNext());

            cursor.close();
        }


        return rentCarList;
    }

    public Offer getBestSale() {
        if (bestBuySalePosition == -1 && this.getAllSales().size() > 0) {
            bestBuySalePosition = (new Random().nextInt(this.getAllSales().size()));
        }

        SQLiteDatabase db = this.getWritableDatabase();

        Offer saleCar = new Offer();
        String query =  "SELECT * FROM " + TABLE_SALE + " LIMIT 1 OFFSET " + bestBuySalePosition;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {




                saleCar.setLicensePlate(cursor.getString(0));
                saleCar.setMatriculationYear(Integer.parseInt(cursor.getString(1)));
                saleCar.setPrice(Double.parseDouble(cursor.getString(2)));
                saleCar.setKm(Integer.parseInt(cursor.getString(3)));
                saleCar.setName(cursor.getString(4));
                saleCar.setCarType(cursor.getString(5));
                saleCar.setEngineType(cursor.getString(6));
                saleCar.setDoors(Integer.parseInt(cursor.getString(7)));
                saleCar.setTrasmission(cursor.getString(8));
                saleCar.setHp(Integer.parseInt(cursor.getString(9)));
                // rentCar.setKw(Integer.parseInt(cursor.getString(10)));
                saleCar.setTorque(Integer.parseInt(cursor.getString(11)));
                saleCar.setCc(Integer.parseInt(cursor.getString(12)));
                saleCar.setNumCylinders(Integer.parseInt(cursor.getString(13)));
                saleCar.setCylindersType(cursor.getString(14));
                saleCar.setTopSpeed(Integer.parseInt(cursor.getString(15)));
                saleCar.setAcc(Double.parseDouble(cursor.getString(16)));
                saleCar.setWeight(Integer.parseInt(cursor.getString(17)));
                saleCar.setImg(cursor.getString(18));







            cursor.close();


        }

        return saleCar;
    }

    public void addSale(Offer saleCar) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues saleValue = new ContentValues();

        saleValue.put("licensePlate", saleCar.getLicensePlate());
        saleValue.put("matriculationYear", saleCar.getMatriculationYear());
        saleValue.put("price", saleCar.getPrice());
        saleValue.put("km", saleCar.getKm());
        saleValue.put("name", saleCar.getName());
        saleValue.put("carType", saleCar.getCarType());
        saleValue.put("engineType", saleCar.getEngineType());
        saleValue.put("doors", saleCar.getDoors());
        saleValue.put("trasmission", saleCar.getTrasmission());
        saleValue.put("hp", saleCar.getHp());
        saleValue.put("torque", saleCar.getTorque());
        saleValue.put("cc", saleCar.getCc());
        saleValue.put("numCylinders", saleCar.getNumCylinders());
        saleValue.put("cylindersType", saleCar.getCylindersType());
        saleValue.put("topSpeed", saleCar.getTopSpeed());
        saleValue.put("acc", saleCar.getAcc());
        saleValue.put("weight", saleCar.getWeight());
        saleValue.put("img", saleCar.getImg());

        db.insert(TABLE_SALE, null, saleValue);

    }


    public void deleteTableSale() {
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteTable = "DELETE FROM " + TABLE_SALE;
        db.execSQL(deleteTable);

    }


}
