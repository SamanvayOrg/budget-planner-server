package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.*;
import org.mbs.budgetplannerserver.domain.code.*;

public class MetadataContractMapper {
    public MetadataContract map(Metadata metadata) {
        MetadataContract metadataContract = new MetadataContract();
        metadata.getFunctionGroups().forEach(functionGroup -> {
            metadataContract.addFunctionGroup(map(functionGroup));
        });
        metadata.getMajorHeadGroups().forEach(majorHeadGroup -> {
            metadataContract.addMajorHeadGroup(map(majorHeadGroup));
        });
        return metadataContract;
    }

    private FunctionGroupContract map(FunctionGroup functionGroup) {
        FunctionGroupContract functionGroupContract = new FunctionGroupContract();
        functionGroupContract.setName(functionGroup.getName());
        functionGroupContract.setCode(functionGroup.getCode());
        functionGroup.getFunctions().forEach(function -> {
            functionGroupContract.addFunction(map(function));
        });
        return functionGroupContract;
    }

    private FunctionContract map(Function function) {
        FunctionContract functionContract = new FunctionContract();
        functionContract.setName(function.getName());
        functionContract.setCode(function.getCode());
        functionContract.setFullCode(function.getFullCode());
        return functionContract;
    }

    private MajorHeadGroupContract map(MajorHeadGroup majorHeadGroup) {
        MajorHeadGroupContract majorHeadGroupContract=new MajorHeadGroupContract();
        majorHeadGroupContract.setName(majorHeadGroup.getName());
        majorHeadGroupContract.setCode(majorHeadGroup.getCode());
        majorHeadGroup.getMajorHeads().forEach(majorHead -> {
            majorHeadGroupContract.addMajorGroup(map(majorHead));
        });
        return majorHeadGroupContract;
    }

    private MajorHeadContract map(MajorHead majorHead) {
        MajorHeadContract majorHeadContract=new MajorHeadContract();
        majorHeadContract.setName(majorHead.getName());
        majorHeadContract.setCode(majorHead.getCode());
        majorHeadContract.setMajorHeadDisplayOrder(majorHead.getDisplayOrder());
        majorHead.getMinorHeads().forEach(minorHead -> {
            majorHeadContract.addMinorHeadContract(map(minorHead));
        });
        return majorHeadContract;
    }

    private MinorHeadContract map(MinorHead minorHead) {
        MinorHeadContract minorHeadContract = new MinorHeadContract();
        minorHeadContract.setName(minorHead.getName());
        minorHeadContract.setCode(minorHead.getCode());
        minorHead.getDetailedHeads().forEach(detailedHead -> {
            minorHeadContract.addMinorHead(map(detailedHead));
        });
        return minorHeadContract;
    }

    private DetailedHeadContract map(DetailedHead detailedHead) {
        DetailedHeadContract detailedHeadContract = new DetailedHeadContract();
        detailedHeadContract.setCode(detailedHead.getCode());
        detailedHeadContract.setName(detailedHead.getName());
        detailedHeadContract.setFullCode(detailedHead.getFullCode());
        return detailedHeadContract;
    }
}
