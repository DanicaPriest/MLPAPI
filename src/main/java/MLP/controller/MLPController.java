package MLP.controller;

import MLP.model.MLPOverview;
import MLP.model.MLPRoot;
import MLP.service.MLPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mlp")
public class MLPController {

    @Autowired
    MLPService mlpService;



    //GET
    //returns one random image
    @RequestMapping("/random")
    public MLPOverview getRandom() {
        return mlpService.getRandom();
    }

    //PUT
    @RequestMapping("/")
    public String loadMLP(@RequestParam(value="ldb", defaultValue="10") String resultNum) {
        mlpService.loadMLP(resultNum);
        return "your database has loaded with " + resultNum + " new Ponies";
    }

    //Create
    @RequestMapping(method = RequestMethod.POST, value = "/")
    public MLPOverview insertNewMLP(@RequestBody MLPOverview results) {
        mlpService.insertMLP(results);
        return results;
    }

    //Read
    //searches for a query then returns the results
    @RequestMapping("/search")
    public MLPRoot searchMLP() {
        return mlpService.searchMLP();
    }

    //Update
    @RequestMapping(method = RequestMethod.PATCH, value = "/")
    public MLPOverview updateMLP(@RequestBody MLPOverview results){
     return mlpService.updateMLP(results);
    }
    //Delete

    @RequestMapping(method = RequestMethod.DELETE, value = "/")
    public MLPOverview deleteMLP(@RequestBody MLPOverview results){
        return mlpService.deleteMLP(results);
    }
}
