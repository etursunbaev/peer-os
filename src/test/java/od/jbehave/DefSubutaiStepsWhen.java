package od.jbehave;

import net.thucydides.core.annotations.Steps;
import od.steps.SubutaiSteps;
import org.jbehave.core.annotations.When;

import java.io.FileNotFoundException;

public class DefSubutaiStepsWhen {

    @Steps
    SubutaiSteps subutaiSteps;

    @When("the user click on the menu item: Environment")
    public void click_on_link_environment(){
        subutaiSteps.clickOnMenuItemEnvironment();
    }

    @When("the user click on the menu item: Blueprint")
    public void click_on_link_blueprint(){
        subutaiSteps.clickOnMenuItemBlueprint();
    }

    @When("the user click on the menu item: Environments")
    public void click_on_link_environments(){
        subutaiSteps.clickOnMenuItemEnvironments();
    }

    @When("the user click on the menu item: Containers")
    public void click_on_link_Containers(){
        subutaiSteps.clickOnMenuItemContainers();
    }

    @When("the user click on the button: Create Blueprint")
    public void click_on_button_create_blueprint(){
        subutaiSteps.clickOnButtonCreateBlueprint();
    }

    @When("the user enter blueprint name: '$blueprintName'")
    public void enter_blueprint_name(String blueprintName){
        subutaiSteps.enterBlueprintName(blueprintName);
    }

    @When("the user enter node name: '$nodeName'")
    public void enter_node_name(String nodeName){
        subutaiSteps.enterNodeName(nodeName);
    }

    @When("the user select template: '$template'")
    public void select_template(String template){
        subutaiSteps.selectTemplate(template);
    }

    @When("the user enter number of containers: '$count'")
    public void enter_number_of_containers(String count){
        subutaiSteps.enterNumberOfContainers(count);
    }

    @When("the user enter SSH group ID: '$id'")
    public void enter_ssh_group_id(String id){
        subutaiSteps.enterSSHGroupID(id);
    }

    @When("the user enter host Group ID: '$id'")
    public void enter_host_group_id(String id){
        subutaiSteps.enterHostGroupID(id);
    }

    @When("the user select quota size: '$quotaSize'")
    public void select_quota_size(String quotaSize){
        subutaiSteps.selectQuotaSize(quotaSize);
    }

    @When("the user click on the button: Add to node list")
    public void click_button_add_to_node_list(){
        subutaiSteps.clickOnButtonAddToNodeList();
    }

    @When("the user click on the button: Create")
    public void click_button_create(){
        subutaiSteps.clickOnButtonCreate();
    }

    @When("the user click on the icon: Build")
    public void click_on_icon_build(){
        subutaiSteps.clickOnIconBuild();
    }

    @When("the user select peer: One")
    public void select_peer_one(){
        subutaiSteps.selectPeer(1);
    }

    @When("the user select peer: Two")
    public void select_peer_two(){
        subutaiSteps.selectPeer(2);
    }

    @When("the user select Strategie: '$strategie'")
    public void select_strategie(String strategie){
        subutaiSteps.selectStrategie(strategie);
    }

    @When("the user click on the button: Place")
    public void click_on_button_place(){
        subutaiSteps.clickOnButtonPlace();
    }

    @When("the user enter environment name: '$name'")
    public void enter_environment_name(String name){
        subutaiSteps.inputEnvironmentName(name);
    }

    @When("the user click on the link: Environment Build List")
    public void click_on_link_environment_build_list(){
        subutaiSteps.clickLinkBuildEnvironmentList();
    }

    @When("the user click on the button: Build")
    public void click_on_button_build(){
        subutaiSteps.clickOnButtonEnvironmentBuild();
    }

    @When("the user click on the button: OK")
    public void click_on_button_ok(){
        subutaiSteps.clickOnButtonOK();
    }

    @When("the user click on the icon: Grow")
    public void click_on_icon_grow(){
        subutaiSteps.clickOnIconGrow();
    }

    @When("the user select environment: Local Environment")
    public void select_environment(){
        subutaiSteps.selectEnvironment(1);
    }

    @When("the user click on the icon: Remove")
    public void click_on_icon_remove(){
        subutaiSteps.clickOnIconRemove();
    }

    @When("the user click on the button: Delete")
    public void click_on_button_delete(){
        subutaiSteps.clickOnButtonDeleteConfirm();
    }

    @When("the user click on the icon: Destroy")
    public void click_on_icon_destroy(){
        subutaiSteps.clickOnIconDestroy();
    }

    @When("the user click on the menu item: Peer Registration")
    public void click_menu_item_peer_registration() {
        subutaiSteps.waitABit(5000);
        subutaiSteps.clickOnMenuPeerRegistration();
    }

    @When("the user click on the link: Create Peer")
    public void click_on_link_create_peer(){
        subutaiSteps.clickOnLinkCreatePeer();
    }

    @When("the user enter peer ip: Second user")
    public void enter_peer_ip_second_user() throws FileNotFoundException {
        subutaiSteps.enterPeerIP();
    }

    @When("the user enter peer key phrase: '$phrase'")
    public void enter_peer_key_phrase(String phrase){
        subutaiSteps.enterPeerKeyPhrase(phrase);
    }

    @When("the user click on the button: Create for peer")
    public void click_on_button_create_for_peer(){
        subutaiSteps.clickOnButtonCreatePeer();
    }

    @When("the user click on the button: Approve")
    public void click_on_button_approve(){
        subutaiSteps.clickOnButtonApprove();
    }

    @When("the user enter approve key phrase: '$phrase'")
    public void enter_peer_approve_key_phrase(String phrase){
        subutaiSteps.enterPeerApproveKeyPhrase(phrase);
    }

    @When("the user click on the button popup: Approve")
    public void click_on_button_popup_approve(){
        subutaiSteps.clickOnButtonPopupApprove();
    }

    @When("the user click on the button: Unregister")
    public void click_on_button_unregister(){
        subutaiSteps.clickOnButtonUnregister();
    }

    @When("the user click on the button: Confirm Unregister")
    public void click_on_button_confirm_unregister(){
        subutaiSteps.clickOnButtonConfirmUnregister();
    }



    @When("the user click on the menu item: Console")
    public void click_menu_item(){
        subutaiSteps.clickLinkConsole();
    }

    @When("the user enter command: '$command'")
    public void enter_command(String command){
        subutaiSteps.enterCommand(command);
    }

    @When("the user select peer executeConsoleCommand: Two")
    public void select_peer_console(){
        subutaiSteps.selectPeerConsole(2);
    }

    @When("the user select any available resource host from select menu")
    public void select_resource_host_from_select_menu(){
        subutaiSteps.selectMenuResourceHost();
    }

    @When("the user enter console command: '$command'")
    public void execute_console_command(String command){
        subutaiSteps.executeConsoleCommand(command);
    }
}