package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.*;
import de.flyndre.fleventsbackend.repositories.OrganizationAccountRepository;
import de.flyndre.fleventsbackend.repositories.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This Service provides usage and logic for the Organization repository and the OrganizationAccount repository.
 * It provides methods for manipulating the data of these repositories.
 * @author Paul Lehmann
 * @version $I$
 */

@Service
public class OrganizationService {
    private OrganizationRepository organizationRepository;
    private OrganizationAccountRepository organizationAccountRepository;
    public OrganizationService(OrganizationRepository organizationRepository, OrganizationAccountRepository organizationAccountRepository){
        this.organizationRepository = organizationRepository;
        this.organizationAccountRepository = organizationAccountRepository;
    }

    /**
     * Returns a list of all organizations.
     * @return List<Organization> list of all organizations
     */
    public List<Organization> getOrganizations(){
        return organizationRepository.findAll();
    }

    /**
     * Returns the organization specified by its id. Throws an Exception if there is no organization with the given id.
     * @param organizationId the id of the organization
     * @return Organization with the given id
     */
    public Organization getOrganizationById(String organizationId){
        Optional<Organization> optional = organizationRepository.findById(organizationId);
        if(!optional.isPresent()){
            throw new NoSuchElementException("Could not found an organization with this id");
        }
        return optional.get();
    }


    /**
     * Returns all accounts of the given organization.
     * @param organization the organization to get the accounts from
     * @return List<FleventsAccoount> list of all accounts
     */
    public List<FleventsAccount> getAccounts(Organization organization){
        return organization.getAccounts().stream().map(organizationAccount -> organizationAccount.getAccount()).collect(Collectors.toList());
    }

    /**
     * Creates the given organization in the database.
     * @param organisation the organization to be created
     * @return the created organization object
     */
    public Organization createOrganisation(Organization organisation){
        organisation.setUuid(null);
        return organizationRepository.save(organisation);
    }

    /**
     * Overwrites the specified organization with the given organization object.
     * @param organizationId the id of the organization to be overwritten
     * @param organisation the new organization object
     * @return the overwritten organization
     */
    public Organization editOrganisation(String organizationId, Organization organisation){
        Organization srcOrganization = organizationRepository.findById(organizationId).get();
        srcOrganization.merge(organisation);
        return organizationRepository.save(srcOrganization);
    }

    /**
     * Removes the given account from the specified organization.
     * @param organization the organization with the account to be removed
     * @param account the account to be removed
     */
    public void removeAccount(Organization organization, FleventsAccount account){
        Optional<OrganizationAccount> optional = organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)).findAny();
        if(!optional.isPresent()){
            throw new IllegalArgumentException("The given account is no part of the given organization");
        }
        organizationAccountRepository.delete(optional.get());
    }

    /**
     * Changes the role of the given account in the specified organization to the new given role. Throws an Exception if the given account is not in the organization or if the given account already has the role to change to.
     * @param organization the organization with the account
     * @param account the account which role has to be changed
     * @param fromRole the previous role of the account
     * @param toRole the new role of the account
     */
    public void changeRole(Organization organization, FleventsAccount account, OrganizationRole fromRole, OrganizationRole toRole){
        Optional<OrganizationAccount> optional = organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)&&organizationAccount.getRole().equals(fromRole)).findAny();
        if(!optional.isPresent()){
            throw new IllegalArgumentException("The given account is no part of the given organization");
        }
        if(organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)&&organizationAccount.getRole().equals(toRole)).findAny().isPresent()){
            throw new IllegalArgumentException("The given account has already the given role");
        }
        OrganizationAccount organizationAccount = optional.get();
        organizationAccount.setRole(toRole);
        organizationAccountRepository.save(organizationAccount);
    }

    /**
     * Returns all organizations where the given account has the role "admin".
     * @param account the account to get the managed organizations from
     * @return list of all managed organizations
     */
    public List<Organization> getManagedOrganization(FleventsAccount account){
        //TODO: Implement
        List<OrganizationAccount> organizationAccounts = organizationAccountRepository.findByAccount_UuidAndRole(account.getUuid(), OrganizationRole.admin);
        List<Organization> organisations = organizationAccounts.stream().map(organizationAccount -> organizationAccount.getOrganization()).collect(Collectors.toList());
        return organisations;
    }

    /**
     * Adds the given account to the specified organization with the specified role. Throws an exception if the account is already part of the organization.
     * @param organization the organization to add the account to
     * @param account the account to be added
     * @param role the role for the account in the organization
     */
    public void addAccountToOrganization(Organization organization, FleventsAccount account, OrganizationRole role){
        if(organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)&&organizationAccount.getRole().equals(role)).findAny().isPresent()){
            throw new IllegalArgumentException("This account is already part of this organization in the specified role");
        }
        organizationAccountRepository.save(new OrganizationAccount(null,organization,account,role));
    }

    /**
     * Deletes the specified organization.
     * @param organization the organization object to be deleted
     */
    public void deleteOrganization(Organization organization){
        organizationRepository.delete(organization);
    }

    public void leaveOrganization(Organization organization, FleventsAccount account){
        Optional<OrganizationAccount> optional = organization.getAccounts().stream().filter(organizationAccount -> organizationAccount.getAccount().equals(account)).findAny();
        if(!optional.isPresent()){
            throw new IllegalArgumentException("The given account is no part of the given organization");
        }
        organizationAccountRepository.delete(optional.get());
    }
}
