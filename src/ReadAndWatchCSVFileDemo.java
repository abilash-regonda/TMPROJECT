import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReadAndWatchCSVFileDemo {
	
	 public void readFilesAndLoadtodb(String path){
		 
		 File folder = new File(path);
		 File[] listOfFiles = folder.listFiles();
		 for (File file : listOfFiles) {
			 readFileAndLoadDB(file);
		 }
	 }
	 
	 public void readFileAndLoadDB(File file) {
		 	BufferedReader br = null;
	        String line = null;
	        String cvsSplitBy = ",";
	        try {
	        	if (file.isFile()) {
	        		br = new BufferedReader(new FileReader(file));
	        	}
	        	int count = 0;
	            while ((line = br.readLine()) != null) {
	            	if(count!=0){
		                String[] row = line.split(cvsSplitBy);
		                if(file.getName().equals("team.csv")) {
		                	saveTeam(row[0]);
		                }
		                if(file.getName().equals("team_skill.csv")) {
		                	saveTeamSkill(row[0],row[1]);
		                }
		                if(file.getName().equals("task.csv")) {
		                	saveTask(row[0],row[1]);
		                }
	                }
	            	 count++;
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                    file.delete();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	 }
	 
	
	    
    private static Connection getDBConnection(){

    	final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
    	final String DB_CONNECTION = "jdbc:oracle:thin:@localhost:1521:orcl";
    	final String DB_USER = "test";
    	final String DB_PASSWORD = "password";
    	
    	Connection dbConnection = null;

		try {
			Class.forName(DB_DRIVER);

		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			dbConnection = DriverManager.getConnection(
                            DB_CONNECTION, DB_USER,DB_PASSWORD);
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
    }
	    
	 public void saveTeam(String teamId) {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;

		String insertTableSQL = "INSERT INTO TEAM"
				+ "(TEAM_ID) VALUES"
				+ "(?)";
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(insertTableSQL);

			preparedStatement.setString(1, teamId);
			// execute insert SQL stetement
			preparedStatement.executeUpdate();

			System.out.println("Record is inserted into TEAM table!");

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
	 }
	 
	 public void saveTeamSkill(String teamId, String teamSkill) {
			Connection dbConnection = null;
			PreparedStatement preparedStatement = null;

			String insertTableSQL = "INSERT INTO TEAM_SKILL"
					+ "(TEAM_ID,SKILL) VALUES"
					+ "(?,?)";
			try {
				dbConnection = getDBConnection();
				preparedStatement = dbConnection.prepareStatement(insertTableSQL);

				preparedStatement.setString(1, teamId);
				preparedStatement.setString(2, teamSkill);
				// execute insert SQL stetement
				preparedStatement.executeUpdate();

				System.out.println("Record is inserted into TEAM_SKILL table!");

			} catch (SQLException e) {

				System.out.println(e.getMessage());

			} finally {

				if (preparedStatement != null) {
					try {
						preparedStatement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				if (dbConnection != null) {
					try {
						dbConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
		 }
	 
	 public void saveTask(String taskId, String skill) {
			Connection dbConnection = null;
			PreparedStatement preparedStatement = null;

			String insertTableSQL = "INSERT INTO TASK"
					+ "(TASK_ID,SKILL) VALUES"
					+ "(?,?)";
			try {
				dbConnection = getDBConnection();
				preparedStatement = dbConnection.prepareStatement(insertTableSQL);

				preparedStatement.setString(1, taskId);
				preparedStatement.setString(2, skill);
				// execute insert SQL stetement
				preparedStatement.executeUpdate();

				System.out.println("Record is inserted into TASK table!");

			} catch (SQLException e) {

				System.out.println(e.getMessage());

			} finally {

				if (preparedStatement != null) {
					try {
						preparedStatement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				if (dbConnection != null) {
					try {
						dbConnection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
		 }
	 
	 
	
	 public static void main(String[] args) {
		 
		 ReadAndWatchCSVFileDemo demo = new ReadAndWatchCSVFileDemo();
         demo.readFilesAndLoadtodb("D://csv_files");
          
	        try {
	            WatchService watcher = FileSystems.getDefault().newWatchService();
	            Path dir = Paths.get("D:/csv_files");
	            dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
	             
	            System.out.println("Watch Service registered for dir: " + dir.getFileName());
	             
	            while (true) {
	                WatchKey key;
	                try {
	                    key = watcher.take();
	                } catch (InterruptedException ex) {
	                    return;
	                }
	                 
	                for (WatchEvent<?> event : key.pollEvents()) {
	                    WatchEvent.Kind<?> kind = event.kind();
	                     
	                    @SuppressWarnings("unchecked")
	                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
	                    Path fileName = ev.context();
	                    
	                    System.out.println(kind.name() + ": " + fileName);
	                    
	                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
	                    	System.out.println("My source file has changed!!!");
	                    	File f = new File("D://csv_files//"+fileName.toString());
	                    	demo.readFileAndLoadDB(f);
	                    }
	                }
	                 
	                boolean valid = key.reset();
	                if (!valid) {
	                    break;
	                }
	            }
	             
	        } catch (IOException ex) {
	            System.err.println(ex);
	        }
	    }

}
