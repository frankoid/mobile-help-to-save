# mobile-help-to-save

[![Build Status](https://travis-ci.org/hmrc/mobile-help-to-save.svg)](https://travis-ci.org/hmrc/mobile-help-to-save) [ ![Download](https://api.bintray.com/packages/hmrc/releases/mobile-help-to-save/images/download.svg) ](https://bintray.com/hmrc/releases/mobile-help-to-save/_latestVersion)

This microservice contains server-side endpoints that supports mobile app-specific Help to Save functionality.

## API

### GET /mobile-help-to-save/startup

Returns information that the mobile app is likely to need each time it starts.

Response format:

```
{
  "shuttering": {
      "shuttered": false,
      "title": "We are shuttered",
      ""message": "Please try again tomorrow"
  },
  // Fine grained feature toggles
  "supportFormEnabled": true,
  // URL of page containing information about the Help to Save scheme
  "infoUrl": "https://www.gov.uk/get-help-savings-low-income",
  // URL of invitation call to action
  "invitationUrl": "/mobile-help-to-save",
  // URL that will redirect enrolled users to the NS&I Help to Save account homepage
  "accessAccountUrl": "/mobile-help-to-save/access-account",
  // User-specific Help to Save data
  "user": {
    // user state, can be NotEnrolled, Enrolled. See <confluence>/display/NGC/Help+to+Save+User+States
    "state": "Enrolled",
    // Account section is present if:
    // - state is "Enrolled"
    // AND
    // - no errors were encountered whilst fetching the account data from NS&I
    "account": {
      "blocked: {
        "unspecified": false
        // In future when we can reliably determine the blocking types we may add the following
        // "payments": true,
        // "withdrawing": false,
        // "receivingBonus": false
      },
      "number": "1000000000001",
      "openedYearMonth": "2018-01",
      "isClosed": true,
      "closureDate": "2018-02-16",
      "closingBalance": 150
      "balance": 150,
      // Amount user has already paid in this month, usually an integer but it's possible to pay in pounds & pence by bank transfer
      "paidInThisMonth": 40.12,
      // Headroom left to save, to achieve any further bonus
      "canPayInThisMonth": 9.88,
      // Should be constant at £50, but having a property means we are protected from further changes
      "maximumPaidInThisMonth": 50,
      // The end date of the month that the "this month" figures refer to.
      // Clients should use this date instead of calculating it themselves to protect against clock skew & race conditions.
      "thisMonthEndDate": "2018-02-28",
      // Start of the next paying-in month of this Help to Save account.
      // Absent if it won't be possible to pay into this account next month 
      // (because the final bonus term will have ended).
      "nextPaymentMonthStartDate": "2018-03-01",
      "accountHolderName": "Testforename Testsurname",
      // Email may be absent (it isn't a required field in the NS&I API we source it from)
      "accountHolderEmail": "test@example.com",
      "bonusTerms": [
        {
          // The amount calculated that the user will receive, two years after they have started the account
          "bonusEstimate": 75,
          "bonusPaid": 0,
          "endDate": "2019-12-31",
          // The date from which the first bonus will be paid, ISO-8601 date
          "bonusPaidOnOrAfterDate": "2020-01-01",
          // balanceMustBeMoreThanForBonus is the savings amount that needs to be exceeded (in this term) to earn a bonus
          // Always zero for first term - only included there for consistency.
          // For the second term this may be inaccurate for the first few days after the second term starts.
          // This is because some payments are deemed to be added after NS&I become aware of them,
          // see <confluence>/display/H2S/HtS+Payment+Processing+Schedule,
          // so the first bonus amount is not known immediately the first term ends 
          "balanceMustBeMoreThanForBonus": 0
        }
        // there may optionally be another bonusTerm object here for the second bonus
      ]
      // What bonus term is the account currently in: "First", "Second" or "AfterFinalTerm"
      "currentBonusTerm": "First"
    }
  }
}
```

#### Errors
If there is a problem obtaining the user-specific data then the `user` object will be replaced with a `userError` object. Other fields (feature flags and shuttering) will be unaffected and still returned:
```
  "supportFormEnabled": true,
  // etc... shuttering and other feature flags omitted for brevity
  "userError": { "code": "GENERAL" }
  // no "user" object
}
```

If there is a problem obtaining the account data then the `user.account` object will be replaced with a `user.accountError` object. Other fields will be unaffected and still returned:
```
  "supportFormEnabled": true,
  // etc... shuttering and other feature flags omitted for brevity
  "user": {
    "state": "Enrolled"
    "accountError": { "code": "GENERAL" }
    // no "account" object
  }
}
```

If the `user.state` is not `Enrolled` or none of the feature flags that
require account data are `true` then no attempt is made to fetch the account
data and neither `user.account` nor `user.accountError` will be present.

#### Shuttering
When the Help to Save section of the app is shuttered then `shuttering.shuttered` will be true and other fields except for feature flags will be omitted:
```
{
  "shuttering": {
    "shuttered": true,
    "title": "Service Unavailable",
    "message": "You’ll be able to use the Help to Save service at 9am on Monday 29 May 2017."
  },
  // Fine grained feature toggles
  "supportFormEnabled": true
}
```

## Testing

To run the tests in this repository:

    sbt test it:test

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
