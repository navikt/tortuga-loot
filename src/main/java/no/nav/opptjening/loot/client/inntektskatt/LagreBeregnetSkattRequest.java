package no.nav.opptjening.loot.client.inntektskatt;

public class LagreBeregnetSkattRequest {
    private String personIdent;
    private String inntektsaar;
    private InntektSKD inntektSKD;

    /**
     * Gets the value of the personIdent property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonIdent() {
        return personIdent;
    }

    /**
     * Sets the value of the personIdent property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setPersonIdent(String value) {
        this.personIdent = value;
    }

    /**
     * Gets the value of the inntektsaar property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getInntektsaar() {
        return inntektsaar;
    }

    /**
     * Sets the value of the inntektsaar property.
     *
     * @param value allowed object is
     * {@link String }
     */
    public void setInntektsaar(String value) {
        this.inntektsaar = value;
    }

    /**
     * Gets the value of the inntektSKD property.
     *
     * @return possible object is
     * {@link InntektSKD }
     */
    public InntektSKD getInntektSKD() {
        return inntektSKD;
    }

    /**
     * Sets the value of the inntektSKD property.
     *
     * @param value allowed object is
     * {@link InntektSKD }
     */
    public void setInntektSKD(InntektSKD value) {
        this.inntektSKD = value;
    }
}
