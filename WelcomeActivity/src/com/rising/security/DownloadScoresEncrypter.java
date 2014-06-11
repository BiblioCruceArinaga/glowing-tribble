package com.rising.security;


/*public class DownloadScoresEncrypter {

	private String Id_Score = "12345678987654321";
	private String User_Token = "12345654321";
	private String path = "/.RisingScores/scores/";
	private Crypto crypto;
	
	public DownloadScoresEncrypter(Context ctx, String token){
		this.User_Token = token;
		crypto = new Crypto(
				  new SharedPrefsBackedKeyChain(ctx),
				  new SystemNativeCryptoLibrary());
	}
	
	
	public void CreateAndInsert(String url){
		 File f = null; 
		 OutputStream os = null;
		 
		 
		 try{
			 f = new File(Environment.getExternalStorageDirectory() + path + FileNameURL(url));
			 os = new FileOutputStream(f);
			 byte[] cipherText = crypto.encrypt(User_Token.getBytes(), null);
			 
			 while(){
				 os.write(cipherText);
			 }
			 
			 Log.i("Encrypt", cipherText.toString());
		 }catch(Exception e){
			 Log.e("Error", e.getMessage());
		 }finally{
			 os.close();			 
		 }
	 }

	 public boolean DescryptAndConfirm(String fichero){
		 File f = null;
		 FileReader fr = null;
		 BufferedReader br = null;
		 String line = "";
		 String descrypt = "";
		 
		 try{
			 f = new File(Environment.getExternalStorageDirectory() + path + fichero);
			 fr = new FileReader(f);
			 br = new BufferedReader(fr);
			 line = br.readLine();	
			 Log.i("Line", line);
			 
		 }catch(Exception e){
			 e.getMessage();
		 }finally{
			 try {
				 br.close();
				 fr.close();
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 }
		 
		 try {
			byte[] decryptText = crypto.decrypt(line.getBytes(), null);
			Log.i("descrypt", descrypt);
		 } catch (Exception e) {
			 e.printStackTrace();
			 Log.e("Fallo Decrypt 2", e.getMessage());
		 } 
		 
		 if(line.equals(descrypt)){
			 return true;
		 }else{
			 return false;
		 }
	 }
	 
	 public String FileNameURL(String urlCompleto){			
			
			int position = urlCompleto.lastIndexOf('/');
			
			String name = urlCompleto.substring(position, urlCompleto.length());
			
			return name;
	 }	
	 
	 public String getSecurityLine(){
		 String rr = "";
		 try {
			rr = decrypt(Id_Score, User_Token);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			Log.i("Resultado Unión", rr);
		}
		return null;
	 }
	
}*/