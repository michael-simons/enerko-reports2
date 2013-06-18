package de.enerko.hre;

import static de.enerko.hre.Unchecker.uncheck;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ConcreteArgument {
	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public final static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	

	private final FormalArgument formalArgument;
	private final String value;
	private final boolean isNull;
	
	
	public ConcreteArgument(FormalArgument formalArgument, String value) {
		this.formalArgument = formalArgument;
		this.value = value;
		this.isNull = this.value == null || this.value.trim().length() == 0;
	}
	
	public void setTo(final PreparedStatement ps) {
		if(formalArgument.dataType == null)
			throw new IllegalArgumentException(String.format("No dataType for argument \"%s\"", formalArgument.name));
		try{
			switch(formalArgument.dataType) {			
				case varchar2:
					if(isNull)
						ps.setNull(this.formalArgument.position, Types.VARCHAR);
					else
						ps.setString(this.formalArgument.position, value);
					break;
				case number:				
					if(isNull)
						ps.setNull(this.formalArgument.position, Types.NUMERIC);
					else
						ps.setBigDecimal(this.formalArgument.position, new BigDecimal(value));
					break;					
				case date:				
					if(isNull)
						ps.setNull(this.formalArgument.position, Types.DATE);
					else
						ps.setDate(this.formalArgument.position, new Date(dateFormat.parse(value).getTime()));
					break;
				case timestamp:				
					if(isNull)
						ps.setNull(this.formalArgument.position, Types.DATE);
					else
						ps.setTimestamp(this.formalArgument.position, new Timestamp(dateTimeFormat.parse(value).getTime()));
					break;
				default:
					throw new IllegalArgumentException(String.format("Datatype \"%s\" is not supported!", formalArgument.dataType));
						
			}
		} catch(NumberFormatException e) {
			throw uncheck(e);
		} catch(ParseException e) {
			throw uncheck(e);
		} catch(SQLException e) {
			throw uncheck(e);
		}
	}	
}