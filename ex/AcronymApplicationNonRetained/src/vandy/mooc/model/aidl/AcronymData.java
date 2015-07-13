package vandy.mooc.model.aidl;

import java.util.List;

/**
 * This "Plain Ol' Java Object" (POJO) class represents data
 * downloaded in Json from the AcronymWebServiceProxy.  It represents
 * the response Json obtained from the Acronym API, e.g., a call to
 * http://www.nactem.ac.uk/software/acromine/dictionary.py?sf=BBC
 * might return the following Json data:
 * 
 * [{"sf": "BBC", "lfs": [{"lf": "British Broadcasting Corporation", "freq": 8,
 * "since": 1986, "vars": [{"lf": "British Broadcasting
 * Corporation", "freq": 8, "since": 1986}]}, {"lf": "backbone
 * cyclic", "freq": 5, "since": 1999, "vars": [{"lf": "backbone
 * cyclic", "freq": 5, "since": 1999}]}, {"lf": "bilateral breast
 * cancer", "freq": 5, "since": 2000, "vars": [{"lf": "bilateral breast
 * cancer", "freq": 4, "since": 2000}, {"lf": "Bilateral breast
 * cancer", "freq": 1, "since": 2006}]}, {"lf": "bone bisphosphonate
 * clearance", "freq": 3, "since": 1992, "vars": [{"lf": "bone bisphosphonate
 * clearance", "freq": 3, "since": 1992}]}, {"lf": "bovine brain capillary",
 * "freq": 3, "since": 1989, "vars": [{"lf": "bovine brain capillary", "freq":
 * 2, "since": 1989}, {"lf": "bovine brain capillaries", "freq": 1, "since":
 * 2000}]}]}]
 */
public class AcronymData {
    /**
     * Various tags corresponding to data downloaded in Json from the
     * Acronym Service.
     */
    final public static String sf_JSON = "sf";
    final public static String lfs_JSON = "lfs";
    /**
     * Various fields corresponding to data downloaded in Json from
     * the Acronym WebService.
     */
    private String sf;
    private List<AcronymExpansion> lfs;

    /**
     * No-op constructor
     */
    public AcronymData() {
    }

    /**
     * Constructor that initializes all the fields of interest.
     */
    public AcronymData(String sf,
                       List<AcronymExpansion> lfs) {
        super();
        this.sf = sf;
        this.lfs = lfs;
    }

    /**
     * @return the acronym
     */
    public String getSf() {
        return sf;
    }

    /**
     * @param acronym
     */
    public void setSf(String sf) {
        this.sf = sf;
    }

    /**
     * @return the List of AcronymData associated with that acronym
     */
    public List<AcronymExpansion> getLfs() {
        return lfs;
    }

    /**
     * @param the
     *            List of AcronymData associated with that acronym
     */
    public void setLfs(List<AcronymExpansion> lfs) {
        this.lfs = lfs;
    }
}
