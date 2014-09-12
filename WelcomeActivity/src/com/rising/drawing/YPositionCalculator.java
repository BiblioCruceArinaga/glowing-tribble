package com.rising.drawing;

public class YPositionCalculator {

	private transient int cooY;
	private transient final Config config;
	
	public YPositionCalculator()
	{
		config = Config.getInstance();
	}
	
	public byte prepararOctava(final byte octava)
	{
		return (byte) (octava > 10 ? octava - 12 : octava);
	}
	
	public int guitarG2Octave3(final byte step, final int margenY)
	{
		switch (step)
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY + config.distanciaLineasPentagrama * 5 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY + config.distanciaLineasPentagrama * 5;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY + config.distanciaLineasPentagrama * 8;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY + config.distanciaLineasPentagrama * 7 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY + config.distanciaLineasPentagrama * 7;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY + config.distanciaLineasPentagrama * 6 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY + config.distanciaLineasPentagrama * 6;
				break;
		}
		
		return cooY;
	}
	
	public int guitarG2Octave4(final byte step, final int margenY)
	{
		switch (step) 
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY + config.distanciaLineasPentagrama * 2;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY + config.distanciaLineasPentagrama + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY + config.distanciaLineasPentagrama * 4 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY + config.distanciaLineasPentagrama * 4;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY + config.distanciaLineasPentagrama * 3 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY + config.distanciaLineasPentagrama * 3;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY + config.distanciaLineasPentagrama * 2 + 
					config.distanciaLineasPentagramaMitad;
				break;
		}
		
		return cooY;
	}
	
	public int guitarG2Octave5(final byte step, final int margenY)
	{
		switch (step) 
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY - config.distanciaLineasPentagrama - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY - config.distanciaLineasPentagrama * 2;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY + config.distanciaLineasPentagrama;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY + config.distanciaLineasPentagramaMitad;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY - config.distanciaLineasPentagramaMitad;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY - config.distanciaLineasPentagrama;
				break;
		}
		
		return cooY;
	}
	
	public int guitarG2Octave6(final byte step, final int margenY)
	{
		switch (step) 
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY - config.distanciaLineasPentagrama * 5;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY - config.distanciaLineasPentagrama * 5 - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY - config.distanciaLineasPentagrama * 2 - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY - config.distanciaLineasPentagrama * 3;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY - config.distanciaLineasPentagrama * 3 - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY - config.distanciaLineasPentagrama * 4;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY - config.distanciaLineasPentagrama * 4 - 
					config.distanciaLineasPentagramaMitad;
				break;
		}
		
		return cooY;
	}
	
	public int pianoG2Octave2(final byte step, final int margenY)
	{
		switch (step) 
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY + config.distanciaLineasPentagrama * 9;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY + config.distanciaLineasPentagrama * 8 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY + config.distanciaLineasPentagrama * 11 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY + config.distanciaLineasPentagrama * 11;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY + config.distanciaLineasPentagrama * 10 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY + config.distanciaLineasPentagrama * 10;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY + config.distanciaLineasPentagrama * 9 + 
					config.distanciaLineasPentagramaMitad;
				break;
		}
		
		return cooY;
	}
	
	public int pianoG2Octave3(final byte step, final int margenY)
	{
		switch (step) 
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY + config.distanciaLineasPentagrama * 5 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY + config.distanciaLineasPentagrama * 5;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY + config.distanciaLineasPentagrama * 8;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY + config.distanciaLineasPentagrama * 7 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY + config.distanciaLineasPentagrama * 7;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY + config.distanciaLineasPentagrama * 6 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY + config.distanciaLineasPentagrama * 6;
				break;
		}
		
		return cooY;
	}
	
	public int pianoG2Octave4(final byte step, final int margenY)
	{
		switch (step) 
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY + config.distanciaLineasPentagrama * 2;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY + config.distanciaLineasPentagrama + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY + config.distanciaLineasPentagrama * 4 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY + config.distanciaLineasPentagrama * 4;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY + config.distanciaLineasPentagrama * 3 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY + config.distanciaLineasPentagrama * 3;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY + config.distanciaLineasPentagrama * 2 + 
					config.distanciaLineasPentagramaMitad;
				break;
		}
		
		return cooY;
	}
	
	public int pianoG2Octave5(final byte step, final int margenY)
	{
		switch (step) 
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY - config.distanciaLineasPentagrama - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY - config.distanciaLineasPentagrama * 2;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY + config.distanciaLineasPentagrama;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY + config.distanciaLineasPentagramaMitad;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY - config.distanciaLineasPentagramaMitad;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY - config.distanciaLineasPentagrama;
				break;
		}
		
		return cooY;
	}
	
	public int pianoG2Octave6(final byte step, final int margenY)
	{
		switch (step) 
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY - config.distanciaLineasPentagrama * 5;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY - config.distanciaLineasPentagrama * 5 - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY - config.distanciaLineasPentagrama * 2 - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY - config.distanciaLineasPentagrama * 3;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY - config.distanciaLineasPentagrama * 3 - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY - config.distanciaLineasPentagrama * 4;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY - config.distanciaLineasPentagrama * 4 - 
					config.distanciaLineasPentagramaMitad;
				break;
		}
		
		return cooY;
	}
	
	public int pianoF4Octave1(final byte step, final int margenY)
	{
		switch (step)
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY + config.distanciaLineasPentagrama * 6 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY + config.distanciaLineasPentagrama * 6;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY + config.distanciaLineasPentagrama * 9;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY + config.distanciaLineasPentagrama * 8 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY + config.distanciaLineasPentagrama * 8;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY + config.distanciaLineasPentagrama * 7 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY + config.distanciaLineasPentagrama * 7;
				break;
		}
		
		return cooY;
	}
	
	public int pianoF4Octave2(final byte step, final int margenY)
	{
		switch (step)
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY + config.distanciaLineasPentagrama * 3;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY + config.distanciaLineasPentagrama * 2 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY + config.distanciaLineasPentagrama * 5 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY + config.distanciaLineasPentagrama * 5;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY + config.distanciaLineasPentagrama * 4 + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY + config.distanciaLineasPentagrama * 4;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY + config.distanciaLineasPentagrama * 3 + 
					config.distanciaLineasPentagramaMitad;
				break;
		}
		
		return cooY;
	}
	
	public int pianoF4Octave3(final byte step, final int margenY)
	{
		switch (step)
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY - config.distanciaLineasPentagramaMitad;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY - config.distanciaLineasPentagrama;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY + config.distanciaLineasPentagrama * 2;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY + config.distanciaLineasPentagrama + 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY + config.distanciaLineasPentagrama;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY + config.distanciaLineasPentagrama - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY;
				break;
		}
		
		return cooY;
	}
	
	public int pianoF4Octave4(final byte step, final int margenY)
	{
		switch (step)
		{
			case 1:
			case 8:
			case 15:
				cooY = margenY - config.distanciaLineasPentagrama * 4;
				break;
	
			case 2:
			case 9:
			case 16:
				cooY = margenY - config.distanciaLineasPentagrama * 4 - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 3:
			case 10:
			case 17:
				cooY = margenY - config.distanciaLineasPentagrama - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 4:
			case 11:
			case 18:
				cooY = margenY - config.distanciaLineasPentagrama * 2;
				break;
	
			case 5:
			case 12:
			case 19:
				cooY = margenY - config.distanciaLineasPentagrama * 2 - 
					config.distanciaLineasPentagramaMitad;
				break;
	
			case 6:
			case 13:
			case 20:
				cooY = margenY - config.distanciaLineasPentagrama * 3;
				break;
	
			case 7:
			case 14:
			case 21:
				cooY = margenY - config.distanciaLineasPentagrama * 3 - 
					config.distanciaLineasPentagramaMitad;
				break;
		}
		
		return cooY;
	}
	
	public int silence(final byte figuracion, final int margenY)
	{
		switch (figuracion)
		{
			case 5:		
			case 6:
			case 7:
			case 8:
			case 9:
				cooY = margenY;
				break;
				
			case 10:
				cooY = margenY + config.distanciaLineasPentagrama + 
					config.distanciaLineasPentagramaMitad + config.ySilencioBlanca;
				break;
				
			case 11:
				cooY = margenY + config.distanciaLineasPentagrama;
				break;
		}
		
		return cooY;
	}
}
