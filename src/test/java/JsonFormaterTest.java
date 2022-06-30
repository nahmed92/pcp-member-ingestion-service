import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.delta.pcpingestion.interservice.tibco.dto.Member;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFormaterTest {
	
	 
	public static void main(String[] args) throws Exception {
	
		String json = Files.readString(Paths.get("C:\\git\\GMMO\\PCP\\pcp-member-ingestion-service\\src\\test\\resources\\sample.json"));
		
		ObjectMapper mapper = new ObjectMapper();
		
		Member m =  mapper.readValue(json.getBytes(), Member.class);
		System.out.println(m);
	}

}
