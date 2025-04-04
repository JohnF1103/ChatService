package flightIQ_microservices.LLM_SVC.Service;


public class ChatServiceTest {
    public void testTranslation() {
        ChatServiceImpl service = new ChatServiceImpl(); 

        String nonTranslated1 = "NonTranslated1"; 
        String translation1 = service.translateNOTAM(nonTranslated1);
        System.out.println(translation1);
    }


    public static void main(String[] args) {
        ChatServiceTest tests = new ChatServiceTest(); 
        tests.testTranslation(); 
    }
}
