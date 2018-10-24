package com.example.siddhiworld.jsonparsestages;

public class Protocol {

    private String name;
    private String description;
    private String totalDuration;
    private String numberofStages;
    private String suitablePersonality;
    private Stages stages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getNumberofStages() {
        return numberofStages;
    }

    public void setNumberofStages(String numberofStages) {
        this.numberofStages = numberofStages;
    }

    public String getSuitablePersonality() {
        return suitablePersonality;
    }

    public void setSuitablePersonality(String suitablePersonality) {
        this.suitablePersonality = suitablePersonality;
    }

    public Stages getStages() {
        return stages;
    }

    public void setStages(Stages stages) {
        this.stages = stages;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("\n name:"+this.name);
        sb.append("\n description:"+this.description);
        sb.append("\n numberofStages:"+this.numberofStages);
        sb.append("\n totalDuration:"+this.totalDuration);
        sb.append("\n suitablePersonality:"+this.suitablePersonality);

        if (this.stages != null) {
            sb.append("\n Stages:" + this.stages.toString());
        }
        return sb.toString();
    }
}
