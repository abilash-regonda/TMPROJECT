import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProccessTaskAssignmentData {

	private static Connection getDBConnection() {

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
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}

	public List<JsonData> getTeam() throws SQLException {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;

		ResultSet teamList = null;
		ResultSet skillList = null;
		ResultSet taskList = null;

		String teamQuery = "select * from TEAM";
		String skillQuery = "select distinct(skill) from TEAM_SKILL";
		String taskQuery = "select * from TASK";

		List<JsonData> datasetList = new ArrayList<>();
		JsonData dataset = null;

		Set<String> teamSet = new HashSet<>();
		Map<String, String> skillMap = new HashMap<>();
		// Set<String,String> teamSet = new HashSet<>();

		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(teamQuery);

			teamList = preparedStatement.executeQuery();

			preparedStatement = dbConnection.prepareStatement(skillQuery);

			skillList = preparedStatement.executeQuery();

			preparedStatement = dbConnection.prepareStatement(taskQuery);

			taskList = preparedStatement.executeQuery();

			// coding

			Map<String, Map<String, Integer>> skillTaskMap = new HashMap<>();

			Map<Integer, String> skillMapset = null;

			ResultSet taskResult = null;
			ResultSet teamResultMap = null;

			Map<String, Integer> teamMapset = null;

			while (skillList.next()) {
				teamMapset = new HashMap<>();
				skillMapset = new HashMap<>();

				String skill = skillList.getString("skill");

				String taskCountQuery = "select task_id from task where skill = ?";

				preparedStatement = dbConnection.prepareStatement(taskCountQuery);
				preparedStatement.setString(1, skill);

				taskResult = preparedStatement.executeQuery();

				int i = 0;
				while (taskResult.next()) {
					String taskId = taskResult.getString("task_id");
					i++;
					skillMapset.put(i, taskId);

				}

				String teamCountQuery = "select team_id from team_skill where skill = ?";

				preparedStatement = dbConnection.prepareStatement(teamCountQuery);
				preparedStatement.setString(1, skill);

				teamResultMap = preparedStatement.executeQuery();

				int j = 0;

				while (teamResultMap.next()) {

					String taskId = teamResultMap.getString("team_id");
					j++;
					teamMapset.put(taskId, j);

				}

				int divideByCount = i / j;
				int count = 1;
				int m = 1;
				for (String team : teamMapset.keySet()) {
					for (int k = count; k <= divideByCount * m; k++) {
						dataset = new JsonData();
						dataset.setSkill(skill);
						dataset.setTeam(team);
						dataset.setTask(skillMapset.get(k));
						datasetList.add(dataset);
						count++;
					}
					m++;
				}

			}

		} catch (SQLException e) {

			System.out.println(e.getMessage());
			e.printStackTrace();

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
		return datasetList;
	}

	public static void main(String[] args) {
		ProccessTaskAssignmentData data = new ProccessTaskAssignmentData();
		try {
			List<JsonData> jsonDatas = data.getTeam();

			for (JsonData data2 : jsonDatas) {
				System.out.println("Task assignment is :" + data2.getTask() + " skill : " + data2.getSkill() + " team :"
						+ data2.getTeam());
			}

			System.out.println(jsonDatas.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
