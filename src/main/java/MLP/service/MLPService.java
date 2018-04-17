package MLP.service;


import MLP.mapper.MLPMapper;
import MLP.model.MLPOverview;
import MLP.model.MLPRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


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
}
