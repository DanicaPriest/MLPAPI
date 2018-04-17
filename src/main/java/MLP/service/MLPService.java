package MLP.service;


import MLP.mapper.MLPMapper;
import MLP.model.MLPOverview;
import MLP.model.MLPRoot;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Service
public class MLPService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    MLPMapper mlpMapper;

    //Search images and return all info
    public MLPRoot searchMLP() {
        String url = "https://derpibooru.org/search.json?q=safe";

        MLPRoot search = restTemplate.getForObject(url, MLPRoot.class);


        return search;
    }

    //get a random image with overview of info
    public MLPOverview getRandom() {
        int max = searchMLP().getTotal();
        int rNum = (int) (Math.random() * (max - 1)) + 1;

        String url = "https://derpibooru.org/images/" + rNum + ".json?q=safe";

        MLPOverview random = restTemplate.getForObject(url, MLPOverview.class);

        //removing anything weird not caught by safe filter
        if (random.getImage().contains("safe")) {
            random.setImage(random.getImage().replaceAll("//", "https://www."));
        }
        else {
            random.setImage("https://i.pinimg.com/originals/4b/89/f7/4b89f79ae535df72b5db9b8ab2d47cf0.png");
            random.setTags("censored");
            random.setUploader("censored");
        }



        return random;
    }

    //loads a number of random images into the database specified by int parameter
    public void loadMLP(String resultNum) {
        int resultint = Integer.parseInt(resultNum);

        for (int i = 0; i < resultint; i++) {
            MLPOverview obj = getRandom();
            obj.setIsActive(1);
            insertMLP(obj);
        }
    }

    //inserts an object into the database
    public void insertMLP(MLPOverview results) {
        mlpMapper.insertMLP(results);
    }

    public MLPOverview updateMLP(MLPOverview results) {
        mlpMapper.makeActive(results);
        return mlpMapper.getMLP(results.getId());
    }

    public MLPOverview deleteMLP(MLPOverview results) {
        mlpMapper.deleteMLP(results);
        return mlpMapper.getMLP(results.getId());
    }

    public void email() throws IOException {
        // Replace sender@example.com with your "From" address.
        // This address must be verified with Amazon SES.
        final String FROM = "danicamaypriest@hotmail.co.uk";

        // Replace recipient@example.com with a "To" address. If your account
        // is still in the sandbox, this address must be verified.
        final String TO = "danicamaypriest@hotmail.co.uk ";

        // The configuration set to use for this email. If you do not want to use a
        // configuration set, comment the following variable and the
        // .withConfigurationSetName(CONFIGSET); argument below.
        //static final String CONFIGSET = "ConfigSet";

        // The subject line for the email.
        final String SUBJECT = "Here's a Random Pony";

        // The HTML body for the email.
        final String HTMLBODY = "<h1>Ponies 4 eva!</h1>"
                + "<img src='" + getRandom().getImage() + "'/>";


        // The email body for recipients with non-HTML email clients.
        final String TEXTBODY = "This email was sent through Amazon SES "
                + "using the AWS SDK for Java.";

        try {
            AmazonSimpleEmailService client =
                    AmazonSimpleEmailServiceClientBuilder.standard()
                            // Replace US_WEST_2 with the AWS Region you're using for
                            // Amazon SES.
                            .withRegion(Regions.US_EAST_1).build();
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(TO))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8").withData(HTMLBODY))
                                    .withText(new Content()
                                            .withCharset("UTF-8").withData(TEXTBODY)))
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(SUBJECT)))
                    .withSource(FROM);
            // Comment or remove the next line if you are not using a
            // configuration set
            // .withConfigurationSetName(CONFIGSET);
            client.sendEmail(request);
            System.out.println("Email sent!");
        } catch (Exception ex) {
            System.out.println("The email was not sent. Error message: "
                    + ex.getMessage());
        }
    }
}
