
Daniella Stokic - dms425

*** GENERAL NOTE: Indentations in the testing descriptions are different user paths. So if there are multiple input options available, 
there are different paths described depending on the options chosen. ***

GENERAL

    - From the root directory...
        * To compile: javac dms425/Enterprise.java dms425/CManager/CManager.java dms425/FManager/FManager.java dms425/PManager/PManager.java dms425/Tenant/Tenant.java
        * To run: java -cp .:ojdbc11.jar dms425.Enterprise

___________________________________________________________________________________________

PROPERTY MANAGER INTERFACE
    - Capabilities:
        * Record Visit Data
        * Record Lease Data
        * Record Move-Out
        * Add Person/Pet to Lease
        * Set Move-Out Date
        * See All Tenant Data
        * See All Lease Data
        * See All Apartment Data

    - How to Test...
        ---------
        * Input '0' into the initial prompt (Exit Interface):
            * Takes you back to the interface selection screen

        ---------
        * Before starting this one --> input '6' (to get all Tenant data)
        * Input '1' into the initial prompt (Record Visit Data):
            * You will be prompted if the person is completely new, or already known:

                * Input '1' (New Person)
                    * You will receive the following prompts in this order:
                        * What is the first name of the prospective tenant?
                            --> Enter 'Jane'
                        * What is the last name of the prospective tenant?
                            --> Enter 'Doe'
                        * What is the income of the prospective tenant?
                            --> Enter '125789'
                        * What is the age of the prospective tenant?
                            --> Enter '26'

                        * What is the Apartment ID of the apartment they visited?
                            --> Enter '19'

                            * The address associated with the given Apartment ID will be printed to the console
                            * A message will be printed to the console saying that the new prospective tenant's visit was successfully recorded.
                            (You can see the new Tenant was successfully created by inputting '6' (see all tenant data) in the option selection 
                            screen)


                * Input '2' (Person Already Known)
                    * You will be prompted for the Tenant ID of the known person:
                        --> Enter '60'

                        * You will be prompted for the Apartment ID they visited:
                            --> Enter '37'

                            * A message saying a visit was recorded for Tenant 60 is printed to the console.

        ---------
        * Before starting this one --> input '6' (to get all Tenant data), and then input '8' (for all Apartment data)
        * Input '2' into the initial prompt (Record Lease Data):
            * You will be prompted for the Apartment ID that you are creating a lease for:
                --> Enter '24'

                * You will be prompted for the Tenant ID of the person signing the lease:
                    --> Enter '57'

                    * You will get a message printed to the console with the newly created lease's ID.
                    * You will get a message printed to the console saying the tenant has been successfully assigned to the lease and has been
                    updated to a current tenant.
        
        ---------
        * Before starting this one --> input '7' in the main selection to get all Lease data
        * Input '3' into the initial prompt (Record Move-Out):
            * You will be prompted for the Lease ID that your are recording a moveout for
                --> Enter '103'

                * The message "Moveout was successfully recorded, and lease data was removed" will be printed to the console.

        ---------
        * Before starting this one --> input '6' (to get all Tenant data), and input '7' (to get all Lease data)
        * Input '4' into the initial prompt (Add Person/Pet to Lease):
            * You will be prompted to indicate whether you wish to add a person to a lease, or a pet

                * Input '1' (Add Person)
                    * You will be prompted for the Tenant ID of the person you want to add to the lease
                        --> Enter '59'

                    * You will be prompted for the Lease ID of the lease you want to add them to
                        --> Enter '102'

                    * The tenant should then be associated with the lease, and a success message will be printed to the console with both the 
                    Tenant ID and the Lease ID.


                * Input '2' (Add Pet)
                    * You will be prompted for the type of pet
                        --> Enter 'Cat'

                    * You will be prompted for the name of the pet
                        --> Enter 'Mittens'

                    * You will be prompted for the Tenant ID of the person who owns the pet
                        --> Enter '52'

                    * The pet should then successfully be added to the database and a success message will be printed to the
                    console.

        ---------
        * Before starting this one --> input '7' (to get all Lease data)
        * Input '5' into the initial prompt (Set Move-Out Date):
            * You will be prompted to enter the Apartment ID that you want to set a moveout day for
                --> Enter '25'

            * You will be prompted to enter the moveout date you wish to set for the apartment
                --> Enter '2023-12-31'

            * Then, a success message will be printed to the screen to reflect the successful update

        ---------
        * Input '6' into the initial prompt (See All Tenant Data):
            * All exisiting prospective and current tenant data is printed to the console

        ---------
        * Input '7' into the initial prompt (See All Lease Data):
            * All exisiting lease data is printed to the console

        ---------
        * Input '8' into the initial prompt (See All Apartment Data):
            * All exisiting apartment data is printed to the console

___________________________________________________________________________________________

TENANT INTERFACE
    - Capabilities:
        * Check Payment Status
        * Make Rental Payment
        * Update Person Data
        * See Current Lease Data
        * Get Tenant ID
        * See/Update Current Amenities

    - How to Test...
        * Upon loading into the interface, you will have a bunch of Tenant Data displayed, this is only for testing purposes.
        All Prospective and Current Tenants are listed, Current Tenants will have their Lease ID displayed at the end of the line.

        ---------
        * Input '0' into the initial prompt (Exit Interface):
            * Takes you back to the interface selection screen

        ---------
        * Input '1' into the initial prompt (Check Payment Status):
            * You will be prompted to enter your Tenant ID
                --> Enter '56'

            * All payment data associated with the Tenant ID will be printed to the console, this will include:
                - All payments made with timestamps and amount displayed
                - Total amount paid-to-date (a sum of all the payments)
                - The amount due because of rent
                - The amount due because of common ammenities
                - The total amount owed
                - The total amount remaining (total owed - total paid)
                
        ---------
        * Input '2' into the initial prompt (Make Rental Payment):
            * You will be prompted to enter your Tenant ID
                --> Enter '56'

            * You will then be prompted to enter the amount you are paying
                --> Enter '1000'

            * You will then by asked to confirm the payment ($1000 in this case)
                * Input '1' (I confirm):
                    * The following message will be printed to the console: 
                        'Your payment of $20 was made successfully'
                            +++ You can validate this was saved in the database selecting "Checking Payment Status" again, and the payment 
                            will be displayed along with the previous data +++

                * Input '2' (I do not want to make this payment):
                    * The payment is aborted, and you get taken back to the Tenant Interface selection

        ---------
        * Input '3' into the initial prompt(Update Person Data):
            * You will be prompted to enter your Tenant ID 
                --> Enter '60'

            * The current person data will be displayed on the console as shown below, and you'll be prompted on whether you want
            to change it or not.
                * First Name: Alycia | Last Name: Marilyn

                * Input '1' (YES):
                    * You will be prompted to enter a new first name, or '{}' if you don't wish to change it.
                        --> Enter '{}'

                    * You will be prompted to enter a new last name, or '{} if you don't wish to change it.
                        --> Enter 'Pluto'

                    * 'Updating your last name' will be printed to the console, and you'll be taken back to the Tenant Interface
                    selection. 
                        * You can check the update worked by inputting '6' and seeing that Tenant ID 60 has the name Alycia Pluto

                * Input '2' (NO):
                    * You will be taken back to the Tenant Interface selection

        ---------
        * Input '4' into the initial prompt (See Current Lease Data):
            * You will be prompted to enter your Tenant ID 
                --> Enter '51'

            * You will then have lease information printed to the console relating to the Tenant ID 51. 
            It should be the same as below:
                Lease ID: 101 | Apartment Number: 385 | Monthly Rent: 1802 | Total Square Feet: 512 | # of baths: 1 | # of beds: 0

        ---------
        * Input '5' into the initial prompt (Get Tenant ID):
            * This is essentially in place of a 'Forgot Your Password' functionality, since the Tenant ID is used as an 
            identification of sorts and it's assumed Tenants should know/have this number.

            * You will be prompted for your first name 
                --> Enter 'Jack'

            * You will be prompted for your last name 
                --> Enter 'Smith'

            * A confirmation will appear 
                --> Enter 'I confirm I am Jack Smith'

            * You should then get a message printed to the console that reads:
                'Your Tenant ID is: 50'

        ---------
        * Input '6' into the initial prompt (See/Update Current Amenities)
            * You will be presented with all protential amenities
            
            * You will be prompted for you Lease ID
                --> Enter '101'

                * The private amenities associated with your apartment are displayed in the console
                * The common amenities available in your building is also displayed in the console

                * You will be prompted if you would like to subscribe to one of the available common amenities
                    * Input '1'
                        * You will be prompted to enter your Tenant ID
                            --> Enter '51'

                            * The common amenities associated with the given Tenant ID will be displayed

                            * You will be prompted to enter the Amenity ID of the common amenity you wish to subscribe to
                                --> Enter '1' (Fitness Center)

                                * You will get a confirmation of your successful subscription displayed to the console
                                * You will get a confirmation of the cost of the subscription displayed to the console

                    * Input '2'
                        * Takes you back to the interface selection screen


___________________________________________________________________________________________

COMPANY MANAGER INTERFACE
    - Capabilities:
        * Add New Property (manually or using data generation)
        * Add Apartment to an Existing Property

    - How to Test...
        * Upon loading into the interface, you will have a bunch of Property Data displayed, this is only for testing purposes.
        All existing property data is listed, you will find Property IDs and Property Addresses here.

        ---------
        * Input '0' into the initial prompt (Exit Interface):
            * Takes you back to the interface selection screen

        ---------
        * Input '1' into the initial prompt (Add New Property):
            * You will be prompted on whether you want to manually add property and apartment data, or generate it instead

            * Input '1' into the prompt (manually add):
                * You will be prompted for the street addreess, town, state abbreviation, and zip code of the new property, I 
                have given some data below, but you can enter what you want. This is what is used to create the full address for the database.
                    --> For Street Address enter: '431 Testing Road'
                    --> For Town enter: 'Redding'
                    --> For State Abbreviation enter: 'MA'
                    --> For Zip Code enter: '19001'

                * You will now be prompted to either add an apartment, or exit apartment creation
                    * Input '1' into the prompt
                        * In the following order...
                            * You will be prompted for the square feet of the apartment 
                                --> enter '850'
                            * You will be prompted for the number of baths in the apartment 
                                --> enter '1'
                            * You will be prompted for the number of beds in the apartment 
                                --> enter '1'
                            * You will be prompted for the apartment number 
                                --> enter '240'
                            * You will be prompted for the monthly rent (and security deposit) 
                                --> enter '3500'
                        
                        * Following entering that data, the current apartment will be printed to the console and added into the database.

                        * The available private amenities will be printed to the console, and you will then be prompted to associate any 
                        private amenities with the created apartment.
                            --> Enter the Amenity Id '5' (In-unit Washer and Dryer) into the prompt
                                * You will get a message saying the ammenity was successfully associated with the apartment

                                * You can choose to either add more amenities by entering their ID or stop by entering '0'. Enter '0'.
                                    --> Enter '0': you wll be taken back to the original apartment selection (either add another apartment, 
                                    or exit creation).

                    * Input '2' into the prompt (exit apartment creation)
                        * You will be taken back to the Company Mananger Interface selections

            * Input '2' into the prompt (generate):
                * You will be prompted for the street addreess, town, state abbreviation, and zip code of the new property, I 
                have given some data below, but you can enter what you want. This is what is used to create the full address for the database.
                    --> For Street Address enter: '958 Algorithms Street'
                    --> For Town enter: 'University'
                    --> For State Abbreviation enter: 'PA'
                    --> For Zip Code enter: '01234'

                * Next you'll be prompted for the number of apartments you want to generate for the property, this can be a max of up to 20.
                    --> Enter '3'  (that should hopefully give you a good sense of the variation in data generation) 

                * Each apartment will then be printed to the console (so you can see what was generated) and added into the database.

        ---------
        * Input '2' into the prompt (Add Apartment to an Existing Property)
            * You will be prompted for the Property ID of the property you want to add apartments to
                --> Enter '6'

                * You will be prompted to either select '1' to add another apartment, or '0' to exit apartment creation
                    * Input '0'
                        * You get taken back to the Company Manager interface selection.

                    * Input '1'
                        * In the following order...
                            * You will be prompted for the square feet of the apartment 
                                --> enter '910'
                            * You will be prompted for the number of baths in the apartment 
                                --> enter '1'
                            * You will be prompted for the number of beds in the apartment 
                                --> enter '2'
                            * You will be prompted for the apartment number 
                                --> enter '357'
                            * You will be prompted for the monthly rent (and security deposit) 
                                --> enter '2294'

                        * You will get an apartment created successfully message printed to the console

                        * You will be prompted if you want to add private amenities to the newly created apartment
                            * Input '0' (You do NOT want to add private amenities)
                                * You are taken back to the "add another apartment or exit apartment creation menu"

                            * Input '1' (You do want to add private amenities)
                                * All available private amenities will be printed to the console
                                * You will be prompted to enter the Amenity ID of the amenity you wish to add to the apartment
                                    --> Enter '5'

                                    * You'll get a success message that the amenity was successfully added to the apartment, and once again get 
                                    prompted to insert the Amenity ID you want to add.
                                        --> Enter '0' (do NOT want to add anymore amenities)
                                            * You get taken back to the Company Manager interface selection screen.

___________________________________________________________________________________________

FINANCIAL MANAGER INTERFACE
    - Capabilities:
        * Get Property Data for a Single Property
        * Get Property Data for a Subset of Properties
        * Get Property Data for all Properties

    - How to Test...
        * Upon loading into the interface, you will have a bunch of Property Data displayed, this is only for testing purposes.
        All existing property data is listed, you will find Property IDs and Property Addresses here.

        ---------
        * Input '0' into the initial prompt (Exit Interface):
            * Takes you back to the interface selection screen

        ---------
        * Input '1' into the initial prompt (Get Property Data for a Single Property):
            * You will be prompted for the ID of the property you want to see:
                --> Enter '1'

            * You should then get the following printed out to the console
                Here is the data for property 1
                id: 1 | address: 1 Smith Store Lane, East Haven, CT 06512

                Earnings to date for Property 1: $2135

        ---------
        * Input '2' into the initial prompt (Get Property Data for a Subset of Properties):
            * You will be prompted for the state abbreviation of the properties you want to see
                --> Enter 'NY'

            * You will then get an output of all properties located in the state, displaying their earnings-to-date broke down by apartment.
            * At the very bottom, the total earnings for all properties located in the state as a whole is displayed.

        ---------
        * Input '3' into the initial prompt (Get Property Data for all Properties):
            * A listing of all the properties in the database should be logged to the console. Each property will have their apartments below them 
            with earnings for each individual apartment displayed.
            * Each property will also have a total earnings across all apartments.
            * At the very bottom, the total earnings across all properties is displayed.
