package com.opencsv;

import javax.sql.rowset.serial.SerialClob;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLXML;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

import static java.sql.Types.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockResultSetBuilder {

   public static ResultSet buildResultSet(ResultSetMetaData metaData, String[] columnValues, int[] columnTypes) throws SQLException {
      ResultSet resultSet = mock(ResultSet.class);
      when(resultSet.getMetaData()).thenReturn(metaData);

      for (int i = 0; i < columnValues.length; i++) {
         setExpectToGetColumnValue(resultSet, i + 1, columnValues[i], columnTypes[i]);
      }

      return wrapResultsetWasNull(columnValues, resultSet);
   }

   private static void setExpectToGetColumnValue(ResultSet rs, int index, String value, int type) throws SQLException {

      switch (type) {
         case BIT:
         case JAVA_OBJECT:
            when(rs.getObject(index)).thenReturn(value);
            break;
         case BOOLEAN:
            when(rs.getBoolean(index)).thenReturn(Boolean.valueOf(value));
            break;
         case BIGINT:
            when(rs.getBigDecimal(index)).thenReturn(value != null ? new BigDecimal(value) : null);
            break;
         case Types.DECIMAL:
         case Types.REAL:
         case Types.NUMERIC:
            when(rs.getBigDecimal(index)).thenReturn(value != null ? new BigDecimal(value) : null);
            break;
         case Types.DOUBLE:
            when(rs.getDouble(index)).thenReturn(value != null ? Double.valueOf(value) : null);
            break;
         case Types.FLOAT:
            when(rs.getFloat(index)).thenReturn(value != null ? Float.valueOf(value) : null);
            break;
         case Types.INTEGER:
         case Types.TINYINT:
         case Types.SMALLINT:
            when(rs.getInt(index)).thenReturn(value != null ? Integer.parseInt(value) : 0);
            break;
         case Types.NVARCHAR:
         case Types.NCHAR:
         case Types.LONGNVARCHAR:
            when(rs.getNString(index)).thenReturn(value);
            break;
         case Types.LONGVARCHAR:
         case Types.VARCHAR:
         case Types.CHAR:
            when(rs.getString(index)).thenReturn(value);
            break;
         case Types.DATE:
            Date date = createDateFromMilliSeconds(value);
            when(rs.getDate(index)).thenReturn(date);
            break;
         case Types.TIME:
            Time time = createTimeFromMilliSeconds(value);
            when(rs.getTime(index)).thenReturn(time);
            break;
         case Types.TIMESTAMP:
            Timestamp ts = createTimeStampFromMilliSeconds(value);
            when(rs.getTimestamp(index)).thenReturn(ts);
            break;
         case Types.NCLOB:
            NClob nc = createNClobFromString(value);
            when(rs.getNClob(index)).thenReturn(nc);
            break;
         case Types.CLOB:
            Clob c = createClobFromString(value);
            when(rs.getClob(index)).thenReturn(c);
            break;

      }

   }

   private static Clob createClobFromString(String value) throws SQLException {
      return value != null ? new SerialClob(value.toCharArray()) : null;
   }

   private static NClob createNClobFromString(String value) throws SQLException {
      return value != null ? new NClobWrapper(new SerialClob(value.toCharArray())) : null;
   }

   private static Date createDateFromMilliSeconds(String value) {
      Date date;

      if (value == null) {
         date = null;
      } else {
         long milliseconds = Long.parseLong(value);
         date = new Date(milliseconds);
      }
      return date;
   }

   private static Time createTimeFromMilliSeconds(String value) {
      Time time;

      if (value == null) {
         time = null;
      } else {
         long milliseconds = Long.parseLong(value);
         time = new Time(milliseconds);
      }
      return time;
   }

   private static Timestamp createTimeStampFromMilliSeconds(String value) {
      Timestamp timestamp;

      if (value == null) {
         timestamp = null;
      } else {
         long milliseconds = Long.parseLong(value);
         timestamp = new Timestamp(milliseconds);
      }
      return timestamp;
   }

   public static ResultSet buildResultSet(String[] header, String[] values, int numRows) throws SQLException {
      ResultSet rs = mock(ResultSet.class);
      ResultSetMetaData rsmd = MockResultSetMetaDataBuilder.buildMetaData(header);

      when(rs.getMetaData()).thenReturn(rsmd);

      for (int i = 0; i < values.length; i++) {
         buildStringExpects(rs, i + 1, values[i], numRows);
      }
      buildNextExpect(rs, numRows);
      return rs;  //To change body of created methods use File | Settings | File Templates.
   }

   private static void buildStringExpects(ResultSet rs, int index, String value, int numRows) throws SQLException {

      if (numRows > 1) {
         String[] columnValues = new String[numRows];
         for (int i = 0; i < numRows - 1; i++) {
            columnValues[i] = value;
         }
         when(rs.getString(index)).thenReturn(value, columnValues);
      } else {
         when(rs.getString(index)).thenReturn(value);
      }
   }

   private static void buildNextExpect(ResultSet rs, int numRows) throws SQLException {
      if (numRows == 1) {
         when(rs.next()).thenReturn(true, false);
      } else {
         Boolean[] nextArray = new Boolean[numRows];
         for (int i = 0; i < numRows; i++) {
            nextArray[i] = i < (numRows - 1);
         }
         when(rs.next()).thenReturn(true, nextArray);
      }
   }

   private static ResultSet wrapResultsetWasNull(String[] columnValues, ResultSet resultSet) {
      return new ResultSet()  {
         int lastColumn = 0;

         public boolean wasNull() throws SQLException {
            return columnValues[lastColumn - 1] == null;
         }
          
         public boolean absolute(int row) throws SQLException {
            return resultSet.absolute(row);
         }

         public void afterLast() throws SQLException {
            resultSet.afterLast();
         }

         public void beforeFirst() throws SQLException {
            resultSet.beforeFirst();
         }

         public void cancelRowUpdates() throws SQLException {
            resultSet.cancelRowUpdates();
         }

         public void clearWarnings() throws SQLException {
            resultSet.clearWarnings();
         }

         public void close() throws SQLException {
            resultSet.close();
         }

         public void deleteRow() throws SQLException {
            resultSet.deleteRow();
         }

         public int findColumn(String columnLabel) throws SQLException {
            return resultSet.findColumn(columnLabel);
         }

         public boolean first() throws SQLException {
            return resultSet.first();
         }

         public Array getArray(int columnIndex) throws SQLException {
            return resultSet.getArray(columnIndex);
         }

         public Array getArray(String columnLabel) throws SQLException {
            return resultSet.getArray(columnLabel);
         }

         public InputStream getAsciiStream(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getAsciiStream(columnIndex);
         }

         public InputStream getAsciiStream(String columnLabel) throws SQLException {
            return resultSet.getAsciiStream(columnLabel);
         }

         @Deprecated
         public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getBigDecimal(columnIndex, scale);
         }

         public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getBigDecimal(columnIndex);
         }

         @Deprecated
         public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
            return resultSet.getBigDecimal(columnLabel, scale);
         }

         public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
            return resultSet.getBigDecimal(columnLabel);
         }

         public InputStream getBinaryStream(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getBinaryStream(columnIndex);
         }

         public InputStream getBinaryStream(String columnLabel) throws SQLException {
            return resultSet.getBinaryStream(columnLabel);
         }

         public Blob getBlob(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getBlob(columnIndex);
         }

         public Blob getBlob(String columnLabel) throws SQLException {
            return resultSet.getBlob(columnLabel);
         }

         public boolean getBoolean(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getBoolean(columnIndex);
         }

         public boolean getBoolean(String columnLabel) throws SQLException {
            return resultSet.getBoolean(columnLabel);
         }

         public byte getByte(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getByte(columnIndex);
         }

         public byte getByte(String columnLabel) throws SQLException {
            return resultSet.getByte(columnLabel);
         }

         public byte[] getBytes(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getBytes(columnIndex);
         }

         public byte[] getBytes(String columnLabel) throws SQLException {
            return resultSet.getBytes(columnLabel);
         }

         public Reader getCharacterStream(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getCharacterStream(columnIndex);
         }

         public Reader getCharacterStream(String columnLabel) throws SQLException {
            return resultSet.getCharacterStream(columnLabel);
         }

         public Clob getClob(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getClob(columnIndex);
         }

         public Clob getClob(String columnLabel) throws SQLException {
            return resultSet.getClob(columnLabel);
         }

         public int getConcurrency() throws SQLException {
            return resultSet.getConcurrency();
         }

         public String getCursorName() throws SQLException {
            return resultSet.getCursorName();
         }

         public Date getDate(int columnIndex, Calendar cal) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getDate(columnIndex, cal);
         }

         public Date getDate(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getDate(columnIndex);
         }

         public Date getDate(String columnLabel, Calendar cal) throws SQLException {
            return resultSet.getDate(columnLabel, cal);
         }

         public Date getDate(String columnLabel) throws SQLException {
            return resultSet.getDate(columnLabel);
         }

         public double getDouble(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getDouble(columnIndex);
         }

         public double getDouble(String columnLabel) throws SQLException {
            return resultSet.getDouble(columnLabel);
         }

         public int getFetchDirection() throws SQLException {
            return resultSet.getFetchDirection();
         }

         public int getFetchSize() throws SQLException {
            return resultSet.getFetchSize();
         }

         public float getFloat(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getFloat(columnIndex);
         }

         public float getFloat(String columnLabel) throws SQLException {
            return resultSet.getFloat(columnLabel);
         }

         public int getHoldability() throws SQLException {
            return resultSet.getHoldability();
         }

         public int getInt(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getInt(columnIndex);
         }

         public int getInt(String columnLabel) throws SQLException {
            return resultSet.getInt(columnLabel);
         }

         public long getLong(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getLong(columnIndex);
         }

         public long getLong(String columnLabel) throws SQLException {
            return resultSet.getLong(columnLabel);
         }

         public ResultSetMetaData getMetaData() throws SQLException {
            return resultSet.getMetaData();
         }

         public Reader getNCharacterStream(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getNCharacterStream(columnIndex);
         }

         public Reader getNCharacterStream(String columnLabel) throws SQLException {
            return resultSet.getNCharacterStream(columnLabel);
         }

         public NClob getNClob(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getNClob(columnIndex);
         }

         public NClob getNClob(String columnLabel) throws SQLException {
            return resultSet.getNClob(columnLabel);
         }

         public String getNString(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getNString(columnIndex);
         }

         public String getNString(String columnLabel) throws SQLException {
            return resultSet.getNString(columnLabel);
         }

         public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getObject(columnIndex, type);
         }

         public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getObject(columnIndex, map);
         }

         public Object getObject(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getObject(columnIndex);
         }

         public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
            return resultSet.getObject(columnLabel, type);
         }

         public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
            return resultSet.getObject(columnLabel, map);
         }

         public Object getObject(String columnLabel) throws SQLException {
            return resultSet.getObject(columnLabel);
         }

         public Ref getRef(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getRef(columnIndex);
         }

         public Ref getRef(String columnLabel) throws SQLException {
            return resultSet.getRef(columnLabel);
         }

         public int getRow() throws SQLException {
            return resultSet.getRow();
         }

         public RowId getRowId(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getRowId(columnIndex);
         }

         public RowId getRowId(String columnLabel) throws SQLException {
            return resultSet.getRowId(columnLabel);
         }

         public SQLXML getSQLXML(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getSQLXML(columnIndex);
         }

         public SQLXML getSQLXML(String columnLabel) throws SQLException {
            return resultSet.getSQLXML(columnLabel);
         }

         public short getShort(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getShort(columnIndex);
         }

         public short getShort(String columnLabel) throws SQLException {
            return resultSet.getShort(columnLabel);
         }

         public Statement getStatement() throws SQLException {
            return resultSet.getStatement();
         }

         public String getString(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getString(columnIndex);
         }

         public String getString(String columnLabel) throws SQLException {
            return resultSet.getString(columnLabel);
         }

         public Time getTime(int columnIndex, Calendar cal) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getTime(columnIndex, cal);
         }

         public Time getTime(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getTime(columnIndex);
         }

         public Time getTime(String columnLabel, Calendar cal) throws SQLException {
            return resultSet.getTime(columnLabel, cal);
         }

         public Time getTime(String columnLabel) throws SQLException {
            return resultSet.getTime(columnLabel);
         }

         public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getTimestamp(columnIndex, cal);
         }

         public Timestamp getTimestamp(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getTimestamp(columnIndex);
         }

         public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
            return resultSet.getTimestamp(columnLabel, cal);
         }

         public Timestamp getTimestamp(String columnLabel) throws SQLException {
            return resultSet.getTimestamp(columnLabel);
         }

         public int getType() throws SQLException {
            return resultSet.getType();
         }

         public URL getURL(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getURL(columnIndex);
         }

         public URL getURL(String columnLabel) throws SQLException {
            return resultSet.getURL(columnLabel);
         }

         @Deprecated
         public InputStream getUnicodeStream(int columnIndex) throws SQLException {
            lastColumn = columnIndex;
            return resultSet.getUnicodeStream(columnIndex);
         }

         @Deprecated
         public InputStream getUnicodeStream(String columnLabel) throws SQLException {
            return resultSet.getUnicodeStream(columnLabel);
         }

         public SQLWarning getWarnings() throws SQLException {
            return resultSet.getWarnings();
         }

         public void insertRow() throws SQLException {
            resultSet.insertRow();
         }

         public boolean isAfterLast() throws SQLException {
            return resultSet.isAfterLast();
         }

         public boolean isBeforeFirst() throws SQLException {
            return resultSet.isBeforeFirst();
         }

         public boolean isClosed() throws SQLException {
            return resultSet.isClosed();
         }

         public boolean isFirst() throws SQLException {
            return resultSet.isFirst();
         }

         public boolean isLast() throws SQLException {
            return resultSet.isLast();
         }

         public boolean isWrapperFor(Class<?> arg0) throws SQLException {
            return resultSet.isWrapperFor(arg0);
         }

         public boolean last() throws SQLException {
            return resultSet.last();
         }

         public void moveToCurrentRow() throws SQLException {
            resultSet.moveToCurrentRow();
         }

         public void moveToInsertRow() throws SQLException {
            resultSet.moveToInsertRow();
         }

         public boolean next() throws SQLException {
            return resultSet.next();
         }

         public boolean previous() throws SQLException {
            return resultSet.previous();
         }

         public void refreshRow() throws SQLException {
            resultSet.refreshRow();
         }

         public boolean relative(int rows) throws SQLException {
            return resultSet.relative(rows);
         }

         public boolean rowDeleted() throws SQLException {
            return resultSet.rowDeleted();
         }

         public boolean rowInserted() throws SQLException {
            return resultSet.rowInserted();
         }

         public boolean rowUpdated() throws SQLException {
            return resultSet.rowUpdated();
         }

         public void setFetchDirection(int direction) throws SQLException {
            resultSet.setFetchDirection(direction);
         }

         public void setFetchSize(int rows) throws SQLException {
            resultSet.setFetchSize(rows);
         }

         public <T> T unwrap(Class<T> arg0) throws SQLException {
            return resultSet.unwrap(arg0);
         }

         public void updateArray(int columnIndex, Array x) throws SQLException {
            resultSet.updateArray(columnIndex, x);
         }

         public void updateArray(String columnLabel, Array x) throws SQLException {
            resultSet.updateArray(columnLabel, x);
         }

         public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
            resultSet.updateAsciiStream(columnIndex, x, length);
         }

         public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
            resultSet.updateAsciiStream(columnIndex, x, length);
         }

         public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
            resultSet.updateAsciiStream(columnIndex, x);
         }

         public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
            resultSet.updateAsciiStream(columnLabel, x, length);
         }

         public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
            resultSet.updateAsciiStream(columnLabel, x, length);
         }

         public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
            resultSet.updateAsciiStream(columnLabel, x);
         }

         public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
            resultSet.updateBigDecimal(columnIndex, x);
         }

         public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
            resultSet.updateBigDecimal(columnLabel, x);
         }

         public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
            resultSet.updateBinaryStream(columnIndex, x, length);
         }

         public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
            resultSet.updateBinaryStream(columnIndex, x, length);
         }

         public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
            resultSet.updateBinaryStream(columnIndex, x);
         }

         public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
            resultSet.updateBinaryStream(columnLabel, x, length);
         }

         public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
            resultSet.updateBinaryStream(columnLabel, x, length);
         }

         public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
            resultSet.updateBinaryStream(columnLabel, x);
         }

         public void updateBlob(int columnIndex, Blob x) throws SQLException {
            resultSet.updateBlob(columnIndex, x);
         }

         public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
            resultSet.updateBlob(columnIndex, inputStream, length);
         }

         public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
            resultSet.updateBlob(columnIndex, inputStream);
         }

         public void updateBlob(String columnLabel, Blob x) throws SQLException {
            resultSet.updateBlob(columnLabel, x);
         }

         public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
            resultSet.updateBlob(columnLabel, inputStream, length);
         }

         public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
            resultSet.updateBlob(columnLabel, inputStream);
         }

         public void updateBoolean(int columnIndex, boolean x) throws SQLException {
            resultSet.updateBoolean(columnIndex, x);
         }

         public void updateBoolean(String columnLabel, boolean x) throws SQLException {
            resultSet.updateBoolean(columnLabel, x);
         }

         public void updateByte(int columnIndex, byte x) throws SQLException {
            resultSet.updateByte(columnIndex, x);
         }

         public void updateByte(String columnLabel, byte x) throws SQLException {
            resultSet.updateByte(columnLabel, x);
         }

         public void updateBytes(int columnIndex, byte[] x) throws SQLException {
            resultSet.updateBytes(columnIndex, x);
         }

         public void updateBytes(String columnLabel, byte[] x) throws SQLException {
            resultSet.updateBytes(columnLabel, x);
         }

         public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
            resultSet.updateCharacterStream(columnIndex, x, length);
         }

         public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
            resultSet.updateCharacterStream(columnIndex, x, length);
         }

         public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
            resultSet.updateCharacterStream(columnIndex, x);
         }

         public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
            resultSet.updateCharacterStream(columnLabel, reader, length);
         }

         public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
            resultSet.updateCharacterStream(columnLabel, reader, length);
         }

         public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
            resultSet.updateCharacterStream(columnLabel, reader);
         }

         public void updateClob(int columnIndex, Clob x) throws SQLException {
            resultSet.updateClob(columnIndex, x);
         }

         public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
            resultSet.updateClob(columnIndex, reader, length);
         }

         public void updateClob(int columnIndex, Reader reader) throws SQLException {
            resultSet.updateClob(columnIndex, reader);
         }

         public void updateClob(String columnLabel, Clob x) throws SQLException {
            resultSet.updateClob(columnLabel, x);
         }

         public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
            resultSet.updateClob(columnLabel, reader, length);
         }

         public void updateClob(String columnLabel, Reader reader) throws SQLException {
            resultSet.updateClob(columnLabel, reader);
         }

         public void updateDate(int columnIndex, Date x) throws SQLException {
            resultSet.updateDate(columnIndex, x);
         }

         public void updateDate(String columnLabel, Date x) throws SQLException {
            resultSet.updateDate(columnLabel, x);
         }

         public void updateDouble(int columnIndex, double x) throws SQLException {
            resultSet.updateDouble(columnIndex, x);
         }

         public void updateDouble(String columnLabel, double x) throws SQLException {
            resultSet.updateDouble(columnLabel, x);
         }

         public void updateFloat(int columnIndex, float x) throws SQLException {
            resultSet.updateFloat(columnIndex, x);
         }

         public void updateFloat(String columnLabel, float x) throws SQLException {
            resultSet.updateFloat(columnLabel, x);
         }

         public void updateInt(int columnIndex, int x) throws SQLException {
            resultSet.updateInt(columnIndex, x);
         }

         public void updateInt(String columnLabel, int x) throws SQLException {
            resultSet.updateInt(columnLabel, x);
         }

         public void updateLong(int columnIndex, long x) throws SQLException {
            resultSet.updateLong(columnIndex, x);
         }

         public void updateLong(String columnLabel, long x) throws SQLException {
            resultSet.updateLong(columnLabel, x);
         }

         public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
            resultSet.updateNCharacterStream(columnIndex, x, length);
         }

         public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
            resultSet.updateNCharacterStream(columnIndex, x);
         }

         public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
            resultSet.updateNCharacterStream(columnLabel, reader, length);
         }

         public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
            resultSet.updateNCharacterStream(columnLabel, reader);
         }

         public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
            resultSet.updateNClob(columnIndex, nClob);
         }

         public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
            resultSet.updateNClob(columnIndex, reader, length);
         }

         public void updateNClob(int columnIndex, Reader reader) throws SQLException {
            resultSet.updateNClob(columnIndex, reader);
         }

         public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
            resultSet.updateNClob(columnLabel, nClob);
         }

         public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
            resultSet.updateNClob(columnLabel, reader, length);
         }

         public void updateNClob(String columnLabel, Reader reader) throws SQLException {
            resultSet.updateNClob(columnLabel, reader);
         }

         public void updateNString(int columnIndex, String nString) throws SQLException {
            resultSet.updateNString(columnIndex, nString);
         }

         public void updateNString(String columnLabel, String nString) throws SQLException {
            resultSet.updateNString(columnLabel, nString);
         }

         public void updateNull(int columnIndex) throws SQLException {
            resultSet.updateNull(columnIndex);
         }

         public void updateNull(String columnLabel) throws SQLException {
            resultSet.updateNull(columnLabel);
         }

         public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
            resultSet.updateObject(columnIndex, x, scaleOrLength);
         }

         public void updateObject(int columnIndex, Object x) throws SQLException {
            resultSet.updateObject(columnIndex, x);
         }

         public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
            resultSet.updateObject(columnLabel, x, scaleOrLength);
         }

         public void updateObject(String columnLabel, Object x) throws SQLException {
            resultSet.updateObject(columnLabel, x);
         }

         public void updateRef(int columnIndex, Ref x) throws SQLException {
            resultSet.updateRef(columnIndex, x);
         }

         public void updateRef(String columnLabel, Ref x) throws SQLException {
            resultSet.updateRef(columnLabel, x);
         }

         public void updateRow() throws SQLException {
            resultSet.updateRow();
         }

         public void updateRowId(int columnIndex, RowId x) throws SQLException {
            resultSet.updateRowId(columnIndex, x);
         }

         public void updateRowId(String columnLabel, RowId x) throws SQLException {
            resultSet.updateRowId(columnLabel, x);
         }

         public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
            resultSet.updateSQLXML(columnIndex, xmlObject);
         }

         public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
            resultSet.updateSQLXML(columnLabel, xmlObject);
         }

         public void updateShort(int columnIndex, short x) throws SQLException {
            resultSet.updateShort(columnIndex, x);
         }

         public void updateShort(String columnLabel, short x) throws SQLException {
            resultSet.updateShort(columnLabel, x);
         }

         public void updateString(int columnIndex, String x) throws SQLException {
            resultSet.updateString(columnIndex, x);
         }

         public void updateString(String columnLabel, String x) throws SQLException {
            resultSet.updateString(columnLabel, x);
         }

         public void updateTime(int columnIndex, Time x) throws SQLException {
            resultSet.updateTime(columnIndex, x);
         }

         public void updateTime(String columnLabel, Time x) throws SQLException {
            resultSet.updateTime(columnLabel, x);
         }

         public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
            resultSet.updateTimestamp(columnIndex, x);
         }

         public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
            resultSet.updateTimestamp(columnLabel, x);
         }

      };
   }

}
