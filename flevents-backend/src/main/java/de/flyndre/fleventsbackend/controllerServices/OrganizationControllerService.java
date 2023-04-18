package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.services.*;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationControllerService {
    private OrganizationService organizationService;
    private FleventsAccountService fleventsAccountService;
    private EventService eventService;
    private final InvitationTokenService invitationTokenService;
    private final EMailServiceImpl eMailService;

    public OrganizationControllerService(OrganizationService organizationService, FleventsAccountService fleventsAccountService, EventService eventService, InvitationTokenService invitationTokenService, EMailServiceImpl eMailService){
        this.organizationService = organizationService;
        this.fleventsAccountService = fleventsAccountService;
        this.eventService = eventService;
        this.invitationTokenService = invitationTokenService;
        this.eMailService = eMailService;
    }

    /**
     * @return List of all organizations
     */
    public List<Organization> getOrganizations(){
        return organizationService.getOrganizations();
    }

    /**
     * @param organizationId the id of the organization to be returned
     * @return the organization object
     */
    public Organization getOrganizationById(String organizationId){
        return organizationService.getOrganizationById(organizationId);
    }

    /**
     * @param organizationId the id of the organization to get the events from
     * @return list of all events from the organization
     */
    public List<Event> getEvents(String organizationId){
        return getOrganizationById(organizationId).getEvents();
    }

    /**
     * get all accounts of an organizations
     * @param organizationId the id of the organization to get all accounts from
     * @return list of accounts
     */
    public List<FleventsAccount> getAccounts(String organizationId){
        return organizationService.getAccounts(getOrganizationById(organizationId));
    }

    /**
     * @param organisation organization object to be created
     * @return Organization that has been created
     */
    public Organization createOrganisation(Organization organisation){
        return organizationService.createOrganisation(organisation);
    }

    /**
     * overwrites an existing organization object with the given organization object
     * @param organizationId the id of the organization to be overwritten
     * @param organisation the organization object with the new organization information
     * @return the updated organization object
     */
    public Organization editOrganisation(String organizationId, Organization organisation){
        return organizationService.editOrganisation(organizationId, organisation);
    }

    /**
     * sends an invitation link in a mail to a specified organization
     * @param organizationId the id of the organization to send an invitation link to
     * @param email the email to send the invitation to
     * @param role the role to set the account with the invitation to
     */
    public void sendInvitation(String organizationId, String email,OrganizationRole role) throws MessagingException {
        InvitationToken token = invitationTokenService.saveToken(new InvitationToken(role.toString()));
        eMailService.sendOrganizationInvitation(getOrganizationById(organizationId),email,token.getToken());
    }

    /**
     * adds an account to an organization after the account owner accepted an invitation
     * @param organizationId the id of the organization to add the account to
     * @param accountId the id of the account to be added to the organization
     * @param token the token to verify the invitation
     */
    public void acceptInvitation(String organizationId,String accountId,String token){
        InvitationToken invitationToken = invitationTokenService.validate(token);
        organizationService.addAccountToOrganization(getOrganizationById(organizationId),fleventsAccountService.getAccountById(accountId),OrganizationRole.valueOf(invitationToken.getRole()));
    }

    /**
     * removes a specified account from a specified organization
     * @param organizationId the id of the organization to remove the account from
     * @param accountId the id of the account to be removed
     */
    public void removeAccount(String organizationId, String accountId){
        organizationService.removeAccount(getOrganizationById(organizationId), fleventsAccountService.getAccountById(accountId));
    }

    /**
     * changes the role of a specified account in a specified organization
     * @param organizationId the id of the organization where to change the accounts role
     * @param accountId the id of the account which role has to be changed
     * @param fromRole the role of the account before the change
     * @param toRole the role to change the account to
     */
    public void changeRole(String organizationId, String accountId, OrganizationRole fromRole, OrganizationRole toRole){
        organizationService.changeRole(getOrganizationById(organizationId),fleventsAccountService.getAccountById(accountId), fromRole,toRole);
    }

    /**
     * creates an event in the specified organization and adds the given account with the role organizer
     * @param event the event to be created
     * @param accountId the id of the account which shall be used to create the event
     * @param organizationId the id of the organization in which to create the event
     * @return event which has been created
     */
    public Event createEvent(String organizationId, Event event, String accountId) {
        //TODO: Implement
        Organization organization = organizationService.getOrganizationById(organizationId);
        FleventsAccount account = fleventsAccountService.getAccountById(accountId);
        return eventService.createEventInOrganization(event, account, organization);
    }
}
